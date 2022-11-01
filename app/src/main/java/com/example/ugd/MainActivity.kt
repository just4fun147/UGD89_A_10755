package com.example.ugd

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import java.lang.Exception
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null
    private var currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager
    private val CHANNEL_ID_1 ="channel_notificiation_01"
    private val notificationId1 = 101

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener{
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
        override fun onSensorChanged(event: SensorEvent) {
            if(event.sensor.type===Sensor.TYPE_PROXIMITY){
                if(event.values[0] === 0f){
                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }
                    else {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    if(mCameraView != null){
                        mCamera?.stopPreview();
                    }
                    mCamera?.release();
                    try {
                        mCamera = Camera.open(currentCameraId)
                    } catch (e: Exception) {
                        Log.d("Error", "Failed to get Camera" + e.message)
                    }
                    if (mCamera != null) {
                        mCameraView = CameraView(applicationContext, mCamera!!)
                        val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                        camera_view.addView(mCameraView)
                    }

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE)as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setUpSensorStuff()
        createNotificationChannel()


        try {
            mCamera = Camera.open(currentCameraId)
        } catch (e: Exception) {
            Log.d("Error", "Failed to get Camera" + e.message)
        }
        if (mCamera != null) {
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }

        if(proximitySensor==null){
            Toast.makeText(this, "No proximity sensor found in device...", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

        }



        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
            imageClose.setOnClickListener{view: View? -> System.exit(0)}
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Modul89_A_10755_PROJECT2 "
            val descriptionText = "Selamat anda sudah berhasil mengerjakan Modul 8 dan 9 "

            val channel1 = NotificationChannel(CHANNEL_ID_1, name,  NotificationManager.IMPORTANCE_DEFAULT).apply{
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }
    private fun sendNotification2(){

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_looks_two_24)
            .setContentTitle("Modul89_A_10755_PROJECT2 ")
            .setContentText("Selamat anda sudah berhasil mengerjakan Modul 8 dan 9 ")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            //Log.d("Main", "onSensorChanged(: sides ${event.values[0]} front/back ${event.values[1]} "))

            val sides = event.values[0]
            val upDown = event.values[1]

            if(upDown.toInt() > 10 || sides.toInt() > 10){
                sendNotification2()
            }
        }
    }

    fun setUpSensorStuff(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }


}