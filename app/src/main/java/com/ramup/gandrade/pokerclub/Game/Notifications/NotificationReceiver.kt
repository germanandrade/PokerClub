package com.ramup.gandrade.pokerclub.Game.Notifications

import ACTION_ACCEPT_TRANSACTION
import ACTION_REJECT_TRANSACTION
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.ramup.gandrade.pokerclub.UserProfile.GameRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class NotificationReceiver : BroadcastReceiver(), KoinComponent {
    val gameRepo by inject<GameRepository>()

    override fun onReceive(context: Context, intent: Intent) {
        val data: Data = intent.getSerializableExtra("data") as Data
        val id: Int = intent.getIntExtra("id", 0)
        if (intent.action == ACTION_ACCEPT_TRANSACTION) {
            val task = when {
                data.requestType.equals(RequestType.BUY.toString()) -> gameRepo.buyEndavans(data.dbId)
                data.requestType.equals(RequestType.LIFESAVER.toString()) -> gameRepo.useLifeSaver(data.dbId)
                data.requestType.equals(RequestType.DEPOSIT.toString()) -> gameRepo.depositEndavans(data.dbId, data.extra!!)
                data.requestType.equals(RequestType.PAY.toString()) -> gameRepo.payDebt(data.dbId, data.extra!!)
                data.requestType.equals(RequestType.WITHDRAW.toString()) -> gameRepo.withdrawEndavans(data.dbId, data.extra!!)
                else -> null
            }
            task!!
                    .addOnSuccessListener {
                        data.success = true
                        gameRepo.sendSuccessNotification(data).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                                .subscribe(
                                        { result ->
                                            Log.d("Result", "FCM responds: ${result}")
                                        },
                                        { error ->
                                            error.printStackTrace()
                                        })
                        Toast.makeText(context, "Sent notification $data", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        data.success = false
                        gameRepo.sendSuccessNotification(data)
                    }
        } else if (intent.action == ACTION_REJECT_TRANSACTION) {
            data.success = false
            gameRepo.sendSuccessNotification(data)

        }
        clearNotification(context, id)
    }

    fun clearNotification(context: Context, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }
}