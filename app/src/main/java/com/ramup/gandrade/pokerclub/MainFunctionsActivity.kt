package com.ramup.gandrade.pokerclub

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.UserProfile.UserProfileActivity
import com.ramup.gandrade.pokerclub.UserProfile.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_main_functions.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel


class MainFunctionsActivity : FragmentActivity() {
    val viewModel by viewModel<UserProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_functions)
    }

    fun buyEndavans(view: View) {
        showMessage(view, "Loading...")
        viewModel.buyEndavans().addOnCompleteListener(this) {
            if (it.isSuccessful) {
                createDialog("Endavans Bougth", "Successful! your debt increased $500")
            }
        }
    }

    fun payDebt(view: View) {
        showMessage(view, "Loading...")
        try {
            viewModel.payDebt().addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    createDialog("Successful Pay Debt", "Now you have no debts")
                }
            }
        } catch (e: Exception) {
            createDialog("error", e?.message.toString())
        }
    }

    fun depositEndavans(view: View) {
        when {
            depositEndavans.text.isEmpty() -> showMessage(view, getString(R.string.empty_deposit))
            else -> {
                var valueToDeposit = depositEndavans.text.toString().toInt()
                viewModel.depositEndavans(valueToDeposit).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        depositEndavans.setText("")
                        createDialog("Successful Deposit", "your account increased E:$valueToDeposit")
                    }
                }
            }
        }

    }


    fun withdrawEndavans(view: View) {
        when {
            withdrawEndavans.text.isEmpty() -> showMessage(view, getString(R.string.empty_withdraw))
            else -> {
                var valueToWithdraw = withdrawEndavans.text.toString().toInt()
                try {
                    withdrawEndavans.setText("")
                    viewModel.withdrawEndavans(valueToWithdraw).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            createDialog("Successful Withdraw", "your account decreased E:$valueToWithdraw")
                        }
                    }
                } catch (e: Exception) {
                    createDialog("error", e?.message.toString())
                }
            }
        }

    }

    private fun createDialog(title: String, message: String) {
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(this)
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    // continue with delete
                    startActivity<UserProfileActivity>()

                }).show()


    }

}
