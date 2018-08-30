package com.ramup.gandrade.pokerclub.Game.Views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.content.res.TypedArrayUtils.getString
import android.view.View
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.Game.Notifications.RequestType
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import kotlinx.android.synthetic.main.withdrawdialog.*

class WithdrawDialog(context: Context?, val gameViewModel: GameViewModel) : Dialog(context), View.OnClickListener {
    override fun onClick(p0: View?) {
        when {
            withdrawEndavans.text.isEmpty() -> showMessage(withdrawEndavans, "Please fill fields!")
            else -> {
                var valueToWithdraw = withdrawEndavans.text.toString().toInt()
                withdrawEndavans.setText("")
                gameViewModel.sendNotification(RequestType.WITHDRAW, valueToWithdraw)
                this.cancel()
            }
        }
    }

    lateinit var activity: Activity
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.withdrawdialog)
        withdraw.setOnClickListener(this)
    }

}