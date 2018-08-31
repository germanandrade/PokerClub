package com.ramup.gandrade.pokerclub.Game.Views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.Game.Notifications.RequestType
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.helper_dialog.*

class HelperDialog(context: Context?, val onclick: (requestType: RequestType, defaultValue: Int?) -> Unit,
                   val message: String, val requestType: RequestType, val stringTitle: String, val buttonTitle: String, val defaultValue: Int?) :
        Dialog(context), View.OnClickListener {
    override fun onClick(button: View?) {
        when {
            defaultValue == null -> onclick(requestType, defaultValue)
            defaultValue != null && editTextHelperDialog.text.isEmpty() -> showMessage(editTextHelperDialog, message)
            else -> onclick(requestType, editTextHelperDialog.text.toString().toInt())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(stringTitle)
        setContentView(R.layout.helper_dialog)
        buttonHelperDialog.setText(buttonTitle)
        if (defaultValue != null) {
            editTextHelperDialog.setText(defaultValue.toString())
        } else {
            editTextHelperDialog.visibility = View.GONE
        }
        buttonHelperDialog.setOnClickListener(this)
    }

}