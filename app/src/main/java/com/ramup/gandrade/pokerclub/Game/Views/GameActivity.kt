package com.ramup.gandrade.pokerclub.Game.Views

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.gandrade.pokerclub.util.TextToImageEncode
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.Game.Notifications.RequestType
import com.ramup.gandrade.pokerclub.Main2Activity
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserAdapter
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import com.ramup.gandrade.pokerclub.UserProfile.User
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.image_dialog.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel


class GameActivity : FragmentActivity() {
    val gameViewModel by viewModel<GameViewModel>()

    lateinit var pauseItem: MenuItem
    lateinit var leaveItem: MenuItem
    lateinit var changeAdminItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        rv_user_list.layoutManager = LinearLayoutManager(this)
        rv_user_list.adapter = UserAdapter(mutableMapOf<String,User>(), this)

        gameViewModel.checkActiveUsers()
        gameViewModel.activeUsers.observe(this, Observer { list ->
            rv_user_list.adapter = UserAdapter(list!!, this)
        })
        gameViewModel.updateAdminToken()
        gameViewModel.adminToken.observe(this, Observer { _ ->
            enableButtons()
        })
    }

    private fun enableButtons() {
        buyButton.isEnabled = true
        depositButton.isEnabled = true
        withdrawButton.isEnabled = true
        payDebtButton.isEnabled = true
    }


    fun setMenu(user: User) {
        if (user.admin) {
            showMessage(rv_user_list, "You're admin!")
            pauseItem.setVisible(true)
            //changeAdminItem.setVisible(true)
        } else {
            leaveItem.setVisible(true)
        }
    }

    fun observeUser() {
        gameViewModel.getUser()
        gameViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                setMenu(user)
            } else {
                //showMessage(rv_user_list, "You're null!")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        pauseItem = menu!!.findItem(R.id.pause)
        leaveItem = menu!!.findItem(R.id.leave)
        changeAdminItem = menu!!.findItem(R.id.changeAdmin)

        val user = gameViewModel.getCurrentUser()
        if (user == null) {
            observeUser()
        } else {
            setMenu(user)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.showqr -> showQr()
            R.id.pause -> pause()
            R.id.leave -> leave()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun leave() {
        gameViewModel.leave()
        gameViewModel.successfulLeave.observe(this, Observer {
            startActivity<Main2Activity>()
            finish()
        })
    }

    private fun pause() {
        gameViewModel.pauseGame()
        gameViewModel.successfulLeave.observe(this, Observer {
            startActivity<Main2Activity>()
            finish()
        })
    }

    private fun showQr() {
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(this)
        }
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.image_dialog, null)
        showMessage(rv_user_list, "llego ${gameViewModel.getCurrentGameId()}")
        view.dialog_imageview.setImageBitmap(TextToImageEncode(gameViewModel.getCurrentGameId()))
        builder.setView(view)
        builder.setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            // continue with delete
        }).show()
    }

    fun buyEndavans(view: View) {
        gameViewModel.sendNotification(RequestType.BUY, null)
        createDialog()
    }

    fun payDebt(view: View) {
        gameViewModel.sendNotification(RequestType.PAY, null)
        createDialog()
    }

    fun depositEndavans(view: View) {
        when {
            depositEndavans.text.isEmpty() -> showMessage(view, getString(R.string.empty_deposit))
            else -> {
                var valueToDeposit = depositEndavans.text.toString().toInt()
                depositEndavans.setText("")
                gameViewModel.sendNotification(RequestType.DEPOSIT, valueToDeposit)
                createDialog()
            }
        }
    }

    fun withdrawEndavans(view: View) {
        when {
            withdrawEndavans.text.isEmpty() -> showMessage(view, getString(R.string.empty_withdraw))
            else -> {
                var valueToWithdraw = withdrawEndavans.text.toString().toInt()
                withdrawEndavans.setText("")
                gameViewModel.sendNotification(RequestType.WITHDRAW, valueToWithdraw)
                createDialog()
            }
        }

    }

    private fun createDialog() {
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(this)
        }
        builder.setTitle("Complete")
                .setMessage("Your request was sent, wait until admin aprove it")
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    // continue with delete

                }).show()


    }

}