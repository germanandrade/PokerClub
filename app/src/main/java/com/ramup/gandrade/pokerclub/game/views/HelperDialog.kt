package com.ramup.gandrade.pokerclub.game.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.game.notifications.RequestType
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.helper_dialog.*

class HelperDialog(context: Context?, val onclick: (requestType: RequestType, defaultValue: Int?) -> Unit,
                   val message: String, val requestType: RequestType, val stringTitle: String, val buttonTitle: String, val defaultValue: Int?, val maxValue: Int?) :
        Dialog(context), View.OnClickListener {
    override fun onClick(button: View?) {
        when {
            defaultValue == null -> {
                onclick(requestType, defaultValue);cancel()
            }
            defaultValue != null && editTextHelperDialog.text.isEmpty() -> showMessage(editTextHelperDialog, message)
            else -> {
                val userValue = editTextHelperDialog.text.toString().toInt()
                when {
                    maxValue != null && userValue > maxValue -> showMessage(editTextHelperDialog, "That value is not valid",Snackbar.LENGTH_SHORT)
                    else -> {
                        onclick(requestType, userValue);cancel()
                    }

                }

            }
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