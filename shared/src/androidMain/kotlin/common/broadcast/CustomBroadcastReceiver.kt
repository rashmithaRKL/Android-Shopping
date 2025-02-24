package common.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CustomBroadcastReceiver(private val context: Context) {
    private val _networkStatus = MutableStateFlow<NetworkStatus>(NetworkStatus.Unknown)
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus

    private val _batteryStatus = MutableStateFlow<BatteryStatus>(BatteryStatus.Unknown)
    val batteryStatus: StateFlow<BatteryStatus> = _batteryStatus

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                when (intent?.action) {
                    ConnectivityManager.CONNECTIVITY_ACTION -> {
                        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                        val activeNetwork = cm?.activeNetworkInfo
                        _networkStatus.value = if (activeNetwork?.isConnected == true) {
                            NetworkStatus.Connected
                        } else {
                            NetworkStatus.Disconnected
                        }
                    }
                }
            } catch (e: Exception) {
                _networkStatus.value = NetworkStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                when (intent?.action) {
                    Intent.ACTION_BATTERY_CHANGED -> {
                        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        val batteryPct = level * 100 / scale.toFloat()
                        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                        
                        _batteryStatus.value = when (status) {
                            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus.Charging(batteryPct)
                            BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus.Full
                            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus.Discharging(batteryPct)
                            else -> BatteryStatus.Unknown
                        }
                    }
                }
            } catch (e: Exception) {
                _batteryStatus.value = BatteryStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun registerReceivers() {
        try {
            context.registerReceiver(
                networkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            context.registerReceiver(
                batteryReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
        } catch (e: Exception) {
            _networkStatus.value = NetworkStatus.Error(e.message ?: "Failed to register receivers")
        }
    }

    fun unregisterReceivers() {
        try {
            context.unregisterReceiver(networkReceiver)
            context.unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            // Log error but don't throw - this is cleanup code
            println("Error unregistering receivers: ${e.message}")
        }
    }
}

sealed class NetworkStatus {
    object Unknown : NetworkStatus()
    object Connected : NetworkStatus()
    object Disconnected : NetworkStatus()
    data class Error(val message: String) : NetworkStatus()
}

sealed class BatteryStatus {
    object Unknown : BatteryStatus()
    object Full : BatteryStatus()
    data class Charging(val percentage: Float) : BatteryStatus()
    data class Discharging(val percentage: Float) : BatteryStatus()
    data class Error(val message: String) : BatteryStatus()
}
