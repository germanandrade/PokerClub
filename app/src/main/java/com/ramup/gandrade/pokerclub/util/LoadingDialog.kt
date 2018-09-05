package com.ramup.gandrade.pokerclub.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.Game.Notifications.RequestType
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.helper_dialog.*

class LoadingDialog(context: Context?) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_dialog)
    }

}