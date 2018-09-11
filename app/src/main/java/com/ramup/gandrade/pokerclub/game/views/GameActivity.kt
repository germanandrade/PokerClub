package com.ramup.gandrade.pokerclub.game.views

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.gandrade.pokerclub.util.TextToImageEncode
import com.example.gandrade.pokerclub.util.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.game.notifications.RequestType
import com.ramup.gandrade.pokerclub.Main2Activity
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.userprofile.GameViewModel
import com.ramup.gandrade.pokerclub.userprofile.User
import com.ramup.gandrade.pokerclub.userprofile.UserAdapter
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.image_dialog.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel

class GameActivity : AppCompatActivity() {
    val gameViewModel by viewModel<GameViewModel>()

    var pauseItem: MenuItem? = null
    var leaveItem: MenuItem? = null
    var changeAdminItem: MenuItem? = null

    var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        rv_user_list.layoutManager = LinearLayoutManager(this)
        rv_user_list.adapter = UserAdapter(mutableMapOf<String, User>(), this)
        gameViewModel.checkActiveUsers()
        gameViewModel.activeUsers.observe(this, Observer { list ->
            currentUser = list!![FirebaseAuth.getInstance().currentUser!!.uid]
            rv_user_list.adapter = UserAdapter(list!!, this)
            if (currentUser != null) disableButtons(currentUser!!)
        })
        try {

            gameViewModel.updateAdminToken()
            gameViewModel.adminToken.observe(this, Observer { _ ->
                if (currentUser != null) disableButtons(currentUser!!)
            })
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT)
            startActivity<Main2Activity>()
            finish()
        }
        supportActionBar!!.show()
    }

    private fun disableButtons(user: User) {
        enableButtons()
        if (user.debt == 0) payDebtButton.isEnabled = false
        if (user.endavans == 0) withdrawButton.isEnabled = false
        if (user.lifeSavers <= 0) useLifeSaver.isEnabled = false
        setTitle("${user.name} playing")
        val actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar_home, null))
        setMenu(user)
    }

    private fun enableButtons() {
        buyButton.isEnabled = true
        depositButton.isEnabled = true
        withdrawButton.isEnabled = true
        payDebtButton.isEnabled = true
        useLifeSaver.isEnabled = true
    }


    fun setMenu(user: User) {
        if (user.admin) {
            showMessage(rv_user_list, "You're admin!", Snackbar.LENGTH_SHORT)
            if (pauseItem != null) pauseItem!!.setVisible(true)
            //changeAdminItem.setVisible(true)
        } else {
            if (leaveItem != null) leaveItem!!.setVisible(true)
            showMessage(rv_user_list, "You're not admin!", Snackbar.LENGTH_SHORT)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        pauseItem = menu!!.findItem(R.id.pause)
        leaveItem = menu!!.findItem(R.id.leave)
        changeAdminItem = menu!!.findItem(R.id.changeAdmin)
        if (currentUser != null) setMenu(currentUser!!)

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


    val onclick: (requestType: RequestType, defaultValue: Int?) -> Unit =
            { requestType, value ->
                gameViewModel.sendNotification(requestType, value,currentUser )
                Toast.makeText(this, "Request sent!", Toast.LENGTH_SHORT).show()
            }

    fun useLifeSaver(view: View) {
        HelperDialog(this, onclick, getString(R.string.empty_deposit), RequestType.LIFESAVER,
                getString(R.string.use_lifesaver), getString(R.string.use), null, null).show()
    }

    fun buyEndavans(view: View) {
        HelperDialog(this, onclick, getString(R.string.empty_deposit), RequestType.BUY,
                getString(R.string.buy_endavans), getString(R.string.buy), null, null).show()
    }

    fun payDebt(view: View) {
        HelperDialog(this, onclick, getString(R.string.empty_deposit), RequestType.PAY
                , getString(R.string.pay_debt), getString(R.string.pay), currentUser?.debt!!, currentUser?.debt!!).show()
    }


    fun depositEndavans(view: View) {
        HelperDialog(this, onclick, getString(R.string.empty_deposit),
                RequestType.DEPOSIT, getString(R.string.deposit_endavans), getString(R.string.deposit), 1000, null).show()
    }

    fun withdrawEndavans(view: View) {
        HelperDialog(this, onclick, getString(R.string.empty_withdraw),
                RequestType.WITHDRAW, getString(R.string.withdraw_endavans), getString(R.string.withdraw), currentUser?.endavans!!, currentUser?.endavans!!).show()
    }
}
