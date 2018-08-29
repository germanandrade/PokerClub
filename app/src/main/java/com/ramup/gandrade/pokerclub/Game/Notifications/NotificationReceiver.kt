package com.ramup.gandrade.pokerclub.Game.Notifications

import ACTION_ACCEPT_TRANSACTION
import ACTION_REJECT_TRANSACTION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.UserProfile.GameRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class NotificationReceiver : BroadcastReceiver(), KoinComponent {
    val gameRepo by inject<GameRepository>()


    override fun onReceive(context: Context, intent: Intent) {


        val data: Data = intent.getSerializableExtra("data") as Data

        if (intent.action == ACTION_ACCEPT_TRANSACTION) {

            val equals = data.requestType.equals(RequestType.BUY.toString())
            val task = when {
                equals -> {
                    gameRepo.buyEndavans(data.dbId)
                }
                data.requestType.equals(RequestType.DEPOSIT.toString()) -> gameRepo.depositEndavans(data.dbId, data.extra!!)
                data.requestType.equals(RequestType.PAY.toString()) -> gameRepo.payDebt(data.dbId)
                data.requestType.equals(RequestType.WITHDRAW.toString()) -> gameRepo.withdrawEndavans(data.dbId, data.extra!!)
                else -> null
            }

            task!!
                    .addOnSuccessListener {
                        data.success = true
                        gameRepo.sendSuccessNotification(data).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    Log.d("Result", "There are ${result} Java developers in Lagos")
                                }, { error ->
                                    error.printStackTrace()
                                })
                        Toast.makeText(context,"Sent notification $data",Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        data.success = false
                        gameRepo.sendSuccessNotification(data)
                    }

        } else if (intent.action == ACTION_REJECT_TRANSACTION) {
        }

    }
}
/*
.addOnSuccessListener {
                                data.success = true
                                gameRepo.sendSuccessNotification(data)
                            }
                            .addOnFailureListener {
                                data.success = false
                                gameRepo.sendSuccessNotification(data)
                            }
 */