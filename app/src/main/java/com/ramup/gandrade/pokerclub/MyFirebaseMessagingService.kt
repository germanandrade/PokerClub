package com.ramup.gandrade.pokerclub

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.startActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "Service"
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        val data = remoteMessage?.getData()
        val requestType = data!!["requestType"]
        val extra = data!!["extra"]
        Log.d(TAG, "From: " + remoteMessage!!.from)
        Log.d(TAG, "Notification Message Body: $requestType $extra")
        //startActivity<NotificationActivity>("message" to myCustomKey)

    }

    private fun sendNotification(remoteMessage: String) {
        val intent = Intent(this, NotificationActivity::class.java)
        intent.putExtra("message",remoteMessage)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION))
        val notificationBuilder = NotificationCompat.Builder(this)
                .setContentText(remoteMessage)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }


}