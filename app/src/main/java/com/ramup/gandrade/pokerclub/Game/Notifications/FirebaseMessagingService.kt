package com.ramup.gandrade.pokerclub.Game.Notifications

import ACTION_ACCEPT_TRANSACTION
import ACTION_REJECT_TRANSACTION
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ramup.gandrade.pokerclub.Game.NotificationCounter
import com.ramup.gandrade.pokerclub.R
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class FirebaseMessagingService() : FirebaseMessagingService(), KoinComponent {

    val TAG = "Service"
    private val CHANNEL = "Channel"
    val notificationCounter by inject<NotificationCounter>()

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val plain = remoteMessage?.getData()
        val data = Data(plain)
        if (data.success == null) {
            sendAdminNotification(data)
        } else {
            sendUserNotification(data)
        }
    }

    private fun sendUserNotification(data: Data) {
        val defaultSoundUri = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION))
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL)
                .setContentText(message(data, data.success!!))
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationCounter.getId(), notificationBuilder.build())
    }

    private fun sendAdminNotification(data: Data) {
        val currentNotificationId = notificationCounter.getId()
        val acceptIntent = Intent(ACTION_ACCEPT_TRANSACTION)
        acceptIntent.putExtra("data", data)
        acceptIntent.putExtra("id", currentNotificationId)
        val acceptPendingIntent = PendingIntent.getBroadcast(this, currentNotificationId, acceptIntent, PendingIntent.FLAG_ONE_SHOT)

        val rejectIntent = Intent(ACTION_REJECT_TRANSACTION)
        rejectIntent.putExtra("data", data)
        rejectIntent.putExtra("id", currentNotificationId)
        val rejectPendingIntent = PendingIntent.getBroadcast(this, currentNotificationId, rejectIntent, PendingIntent.FLAG_ONE_SHOT)

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
        notificationManager.notify(currentNotificationId, notificationBuilder.build())
    }

    fun message(data: Data, success: Boolean): String {
        return "${if (success) "Succesfully" else "Failed"} ${data.requestType} ${if (data.extra != null) data.extra else ""}"
    }


}