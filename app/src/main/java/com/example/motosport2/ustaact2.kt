package com.example.motosport2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ustaact2 : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroSensor: Sensor? = null
    private var accelSensor: Sensor? = null
    private var magnetSensor: Sensor? = null
    private lateinit var gyroTextView: TextView
    private lateinit var accelTextView: TextView
    private lateinit var magnetTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ustaact2)

        gyroTextView = findViewById(R.id.gyroTextView)
        accelTextView = findViewById(R.id.accelTextView)
        magnetTextView = findViewById(R.id.magnetTextView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()
        gyroSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        magnetSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nie jesteśmy zainteresowani tym zdarzeniem w tym przypadku
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {
                Sensor.TYPE_GYROSCOPE -> {
                    val gyroX = event.values[0]
                    val gyroY = event.values[1]
                    val gyroZ = event.values[2]
                    gyroTextView.text = "Żyroskop: X=$gyroX, Y=$gyroY, Z=$gyroZ"
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    val accelX = event.values[0]
                    val accelY = event.values[1]
                    val accelZ = event.values[2]
                    accelTextView.text = "Akcelerometr: X=$accelX, Y=$accelY, Z=$accelZ"
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    val magnetX = event.values[0]
                    val magnetY = event.values[1]
                    val magnetZ = event.values[2]
                    magnetTextView.text = "Magnetometr: X=$magnetX, Y=$magnetY, Z=$magnetZ"
                }
            }
        }
    }
}

