package common.telephony

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.PhoneStateListener
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TelephonyManagerWrapper(private val context: Context) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    fun hasPhonePermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasSmsPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getDeviceId(): String? {
        return try {
            if (hasPhonePermissions()) {
                @Suppress("DEPRECATION")
                telephonyManager.deviceId
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error getting device ID: ${e.message}")
            null
        }
    }

    fun getCallState(): Flow<CallState> = callbackFlow {
        if (!hasPhonePermissions()) {
            trySend(CallState.Error("Phone permission not granted"))
            close()
            return@callbackFlow
        }

        val phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                val callState = when (state) {
                    TelephonyManager.CALL_STATE_IDLE -> CallState.Idle
                    TelephonyManager.CALL_STATE_RINGING -> CallState.Ringing(phoneNumber)
                    TelephonyManager.CALL_STATE_OFFHOOK -> CallState.InCall
                    else -> CallState.Unknown
                }
                trySend(callState)
            }
        }

        try {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            trySend(CallState.Error(e.message ?: "Unknown error"))
        }

        awaitClose {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
    }

    fun sendSms(phoneNumber: String, message: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!hasSmsPermissions()) {
            onError("SMS permission not granted")
            return
        }

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Failed to send SMS")
        }
    }

    fun getNetworkOperatorName(): String {
        return try {
            telephonyManager.networkOperatorName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getPhoneType(): PhoneType {
        return when (telephonyManager.phoneType) {
            TelephonyManager.PHONE_TYPE_GSM -> PhoneType.GSM
            TelephonyManager.PHONE_TYPE_CDMA -> PhoneType.CDMA
            TelephonyManager.PHONE_TYPE_NONE -> PhoneType.NONE
            else -> PhoneType.UNKNOWN
        }
    }
}

sealed class CallState {
    object Idle : CallState()
    object InCall : CallState()
    data class Ringing(val phoneNumber: String?) : CallState()
    object Unknown : CallState()
    data class Error(val message: String) : CallState()
}

enum class PhoneType {
    GSM,
    CDMA,
    NONE,
    UNKNOWN
}
