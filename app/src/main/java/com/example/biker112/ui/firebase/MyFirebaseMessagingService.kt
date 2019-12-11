package com.example.biker112.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {


    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        try{
            Log.d(TAG, "From: ${remoteMessage?.from}")
            remoteMessage?.data?.isNotEmpty()?.let {
                Log.d(TAG, "Message data payload: " + remoteMessage.data)
                /* Check if data needs to be processed by long running job */
                if ( true) {
                    // For long-running tasks (10 seconds or more) use WorkManager.
                    scheduleJob()
                    //SharedPreferencesManager.getInstance(this).setAccessKey(this, remoteMessage.notification?.body)
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())

                    saveNotificationDataTask(
                        "Caleido",
                        remoteMessage.notification?.body,
                        date,
                        "time"
                    ).execute((null))

                    //sendNotification(remoteMessage.notification?.body.toString())
                } else {
                    // Handle message within 10 seconds
                    handleNow()
                }
            }

            // Check if message contains a notification payload.
            remoteMessage?.notification?.let {
                Log.d(TAG, "Message Notification Body: ${it.body}")
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")
        //SharedPreferencesManager.getInstance(this).setFCMToken(this,token)
        sendRegistrationToServer(token)
    }


    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]

//        val intent = Intent(this, DashBoardActivity::class.java)
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }


    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }


//    private fun sendNotification(messageBody: String) {
//        val intent = Intent(this, MainMenu::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//            PendingIntent.FLAG_ONE_SHOT)
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//
//            .setContentTitle(getString(R.string.fcm_message))
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setSmallIcon(R.drawable.ic_launcher_background)
////                .setSound(defaultSoundUri)
////                .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
//    }


    inner class saveNotificationDataTask internal constructor(
                                                                var notification_title: String,
                                                                var notification_body: String?,
                                                                var notification_date: String,
                                                                var notification_time: String


    ) :
        AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            try {
//                userService = DbServiceImpl(this@MyFirebaseMessagingService)
//                var user = NotificationData()
//                user.notify_title = notification_title
//                user.notify_body = notification_body
//                user.notify_date = notification_date
//                user.notify_time = notification_time
//                userService?.insertNotificationData(user)
//                notificationDataList = userService!!.notificationData
//                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                return false
            }

            return true
        }
        override fun onPostExecute(success: Boolean?) {
            //subscribeUi(adapter)
        }
    }
}
