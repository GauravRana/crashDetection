package com.example.biker112.ui.startRide;

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.example.biker112.R
import java.lang.Process
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.R.attr.colorAccent
import android.app.*
import android.graphics.Color
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.NotificationCompat
import com.example.biker112.ui.login.MainActivity
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

class CrashDetectionService : Service() , SensorEventListener {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var sensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private val gravity = FloatArray(3)
    private val linear_acceleration = FloatArray(3)
    private var totalAcceleration: Double = 0.0
    private var lastUpdate: Long = -1
    private val CHECK_INTERVAL = 100 // [msec]

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
//        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
//            start()
//
//            // Get the HandlerThread's Looper and use it for our Handler
//            serviceLooper = looper
//            serviceHandler = ServiceHandler(looper)
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        }
        else {
            startForeground(1,  Notification ());
        }
        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager?.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }


    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
        val channelName = "My Background Service"
        val chan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        chan.setLightColor(Color.BLUE)
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assert(manager != null)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        sensorManager?.unregisterListener(this);
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        sensorManager?.unregisterListener(this);
    }


    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    //Calculate only Linear acceleration
    override fun onSensorChanged(event: SensorEvent?) {
        val curTime = System.currentTimeMillis()
        if (event?.sensor?.getType() == Sensor.TYPE_ACCELEROMETER) {
            if ((curTime - lastUpdate) > CHECK_INTERVAL) {
                val alpha: Float = 0.8f
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime
                linear_acceleration[0] = event!!.values[0]
                linear_acceleration[1] = event!!.values[1]
                linear_acceleration[2] = event!!.values[2]
                checkFallX(
                    Math.abs(linear_acceleration[0]),
                    Math.abs(linear_acceleration[1]),
                    Math.abs(linear_acceleration[2])
                )
            }
        }
    }


    //FALL DETECTION LOGIC
    fun checkFallX(x: Float, y: Float, z: Float) {
        // //print(abs(x),abs(y),abs(z))
        val xd: Double
        val yd: Double
        val zd: Double

        xd = x.toDouble()
        yd = x.toDouble()
        zd = x.toDouble()

        totalAcceleration = Math.sqrt((xd * xd) + (yd * yd) + (zd * zd))
        //Toast.makeText(context!!, totalAcceleration.toString(), Toast.LENGTH_LONG).show()
        Log.d("TOTAL ACC", totalAcceleration.toString());
        //if (totalAcceleration > 10 )
        if (totalAcceleration > 50 ) {
            Log.d("FALL", "ITS FALLLLLLLLLLLLLLLL");
            //Toast.makeText(context!!, "ITS FALLLLLLLLLLLLLLLL", Toast.LENGTH_LONG).show()
            sendBroadcast(true)
        }
    }


    //SEND BROADCAST back to Activity
    private fun sendBroadcast(success: Boolean) {
        val intent = Intent("message") //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("success", success)
        //processStartNotification()
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    //Send a notification in notification builder
    private fun processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        val builder = NotificationCompat.Builder(this)
        builder.setContentTitle("ALERT")
            .setAutoCancel(true)
            .setColor(resources.getColor(R.color.colorAccent))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("Its FALLLLLLLLLLLLLLLLLLLLL")

        val pendingIntent = PendingIntent.getActivity(
            this,
            2,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)

        val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, builder.build())
    }
}
