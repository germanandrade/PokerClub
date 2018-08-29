package com.ramup.gandrade.pokerclub.Game.Notifications

import ACTION_ACCEPT_TRANSACTION
import ACTION_REJECT_TRANSACTION
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ramup.gandrade.pokerclub.R
import android.os.Looper


class MyFirebaseMessagingService() : FirebaseMessagingService() {
    val TAG = "Service"
    val NOTFICATION_ID = 0
    private val CHANNEL = "Channel"
    val URL = "https://developer.android.com/design/patterns/notifications.html";


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        val plain =remoteMessage?.getData()
        Log.d(TAG, "Plain: " + plain)

        val data: Data = Data(plain)
        Log.d(TAG, "From: " + remoteMessage!!.from)
        Log.d(TAG, "Notification Message Body: ${data.token}")


        //startActivity<NotificationActivity>()
        if (data.success == null) {
            sendAdminNotification(data)
        } else {
            sendUserNotification(data, data.success!!)
        }

    }

    fun message(data: Data, success: Boolean): String {
        return "${if (success) "Succesfully" else "Failed"} ${data.requestType} ${if (data.extra != null) data.extra else ""}"
    }

    private fun sendUserNotification(data: Data, success: Boolean) {
        val defaultSoundUri = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION))
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL)
                .setContentText(message(data, success))
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTFICATION_ID, notificationBuilder.build())
    }


    private fun sendAdminNotification(data: Data) {


        val acceptIntent = Intent(ACTION_ACCEPT_TRANSACTION)
        acceptIntent.putExtra("data", data)
        val acceptPendingIntent = PendingIntent.getBroadcast(this, NOTFICATION_ID, acceptIntent, PendingIntent.FLAG_ONE_SHOT)

        val rejectIntent = Intent(ACTION_REJECT_TRANSACTION)
        rejectIntent.putExtra("data", data)
        val rejectPendingIntent = PendingIntent.getBroadcast(this, NOTFICATION_ID, rejectIntent, PendingIntent.FLAG_ONE_SHOT)


        val defaultSoundUri = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION))
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL).setContentText(data.toString())
                .setAutoCancel(true)
                .addAction(R.drawable.ic_accept, "Accept", acceptPendingIntent)
                .addAction(R.drawable.ic_reject, "Reject", rejectPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTFICATION_ID, notificationBuilder.build())
    }


}