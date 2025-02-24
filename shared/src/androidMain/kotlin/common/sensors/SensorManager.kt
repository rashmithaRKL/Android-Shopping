package common.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class DeviceSensorManager(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometerChannel = Channel<AccelerometerData>(Channel.UNLIMITED)
    private val gyroscopeChannel = Channel<GyroscopeData>(Channel.UNLIMITED)
    private val lightChannel = Channel<Float>(Channel.UNLIMITED)

    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val data = AccelerometerData(event.values[0], event.values[1], event.values[2])
            accelerometerChannel.trySend(data)
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val gyroscopeListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val data = GyroscopeData(event.values[0], event.values[1], event.values[2])
            gyroscopeChannel.trySend(data)
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val lightListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            lightChannel.trySend(event.values[0])
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun startAccelerometerUpdates(): Flow<AccelerometerData>? {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        return if (accelerometer != null) {
            sensorManager.registerListener(
                accelerometerListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            accelerometerChannel.receiveAsFlow()
        } else null
    }

    fun startGyroscopeUpdates(): Flow<GyroscopeData>? {
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        return if (gyroscope != null) {
            sensorManager.registerListener(
                gyroscopeListener,
                gyroscope,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            gyroscopeChannel.receiveAsFlow()
        } else null
    }

    fun startLightSensorUpdates(): Flow<Float>? {
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        return if (lightSensor != null) {
            sensorManager.registerListener(
                lightListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            lightChannel.receiveAsFlow()
        } else null
    }

    fun stopSensorUpdates() {
        sensorManager.unregisterListener(accelerometerListener)
        sensorManager.unregisterListener(gyroscopeListener)
        sensorManager.unregisterListener(lightListener)
    }
}

data class AccelerometerData(
    val x: Float,
    val y: Float,
    val z: Float
)

data class GyroscopeData(
    val x: Float,
    val y: Float,
    val z: Float
)
