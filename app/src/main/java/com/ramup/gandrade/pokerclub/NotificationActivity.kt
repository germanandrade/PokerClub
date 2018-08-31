package com.ramup.gandrade.pokerclub

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.ramup.gandrade.pokerclub.Game.Notifications.Data
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : FragmentActivity() {

    val ACTION_ACCEPT_TRANSACTION = "com.rampup.gandrade.pokerclub.game.notifications.myfirebasemessagingservice.ACTION_ACCEPT_TRANSACTION"
    val ACTION_REJECT_TRANSACTION = "com.rampup.gandrade.pokerclub.game.notifications.myfirebasemessagingservice.ACTION_REJECT_TRANSACTION"

    val TAG = NotificationActivity::class.java.simpleName

    val NOTFICATION_ID = 0
    val CHANNEL = "Channel"

    lateinit var myNotificationReceiver: NotificationReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notification)
        textView.setText(intent.getStringExtra("message"))

        myNotificationReceiver = NotificationReceiver()
        val filter = IntentFilter()
        filter.addAction(ACTION_ACCEPT_TRANSACTION)
        filter.addAction(ACTION_REJECT_TRANSACTION)
        registerReceiver(myNotificationReceiver,filter)

        sendNotification(Data("a", "b", "c", "d", null))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myNotificationReceiver)
    }

    private fun sendNotification(data: Data) {


        val acceptIntent = Intent(ACTION_ACCEPT_TRANSACTION)
        val acceptPendingIntent = PendingIntent.getBroadcast(applicationContext, NOTFICATION_ID, acceptIntent, PendingIntent.FLAG_ONE_SHOT)

        val rejectIntent = Intent(ACTION_REJECT_TRANSACTION)
        //rejectIntent.putExtra("data", data)
        val rejectPendingIntent = PendingIntent.getBroadcast(applicationContext, NOTFICATION_ID, rejectIntent, PendingIntent.FLAG_ONE_SHOT)



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

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("OTRO_TAG", "onReceive")
        }
    }
}

