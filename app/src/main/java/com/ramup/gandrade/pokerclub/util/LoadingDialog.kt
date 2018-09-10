package com.ramup.gandrade.pokerclub.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.ramup.gandrade.pokerclub.R

class LoadingDialog(context: Context?) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_dialog)
    }

}