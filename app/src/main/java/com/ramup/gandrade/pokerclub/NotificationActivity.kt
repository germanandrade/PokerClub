package com.ramup.gandrade.pokerclub

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        textView.setText(intent.getStringExtra("message"))
    }


}
