package com.ramup.gandrade.pokerclub.game.views

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.gandrade.pokerclub.util.TextToImageEncode
import com.example.gandrade.pokerclub.util.ifNotNull
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.Main2Activity
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.game.GameViewModel
import com.ramup.gandrade.pokerclub.game.notifications.RequestType
import com.ramup.gandrade.pokerclub.userprofile.User
import com.ramup.gandrade.pokerclub.userprofile.UserAdapter
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.image_dialog.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel

class GameActivity : AppCompatActivity() {
    private val gameViewModel by viewModel<GameViewModel>()
    private lateinit var pauseItem: MenuItem
    private lateinit var leaveItem: MenuItem
    private lateinit var changeAdminItem: MenuItem
    private var currentUser: User? = null
    private val TAG = GameActivity::class.simpleName
    private lateinit var myActionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        rv_user_list.layoutManager = LinearLayoutManager(this)
        rv_user_list.adapter = UserAdapter(mutableMapOf<String, User>(), this)
        gameViewModel.checkActiveUsers()
        gameViewModel.activeUsers.observe(this, Observer { list ->
            val firebaseUser = gameViewModel.getFirebaseUser()
            ifNotNull(list, firebaseUser) { mList, mFirebaseUser ->
                run {
                    currentUser = mList[mFirebaseUser.uid]
                    rv_user_list.adapter = UserAdapter(mList, this)
                    currentUser?.let { disableButtons(it) }
                }
            }

        })
        try {

            gameViewModel.updateAdminToken()
            gameViewModel.adminToken.observe(this, Observer {
                currentUser?.let { disableButtons(it) }
            })
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT)
            startActivity<Main2Activity>()
            finish()
        }
        myActionBar = requireNotNull(supportActionBar) { "supportActionBar was null at $TAG" }
        myActionBar.show()
    }


    private fun disableButtons(user: User) {
        enableButtons()
        if (user.debt == 0) payDebtButton.isEnabled = false
        if (user.endavans == 0) withdrawButton.isEnabled = false
        if (user.lifeSavers <= 0) useLifeSaver.isEnabled = false
        setTitle("${user.name} playing")
        myActionBar.setDisplayShowCustomEnabled(true);
        myActionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar_home, null))
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
            if (::pauseItem.isInitialized) {
                pauseItem.setVisible(true)
            }
            //changeAdminItem.setVisible(true)
        } else {
            if (::leaveItem.isInitialized) {
                leaveItem.setVisible(true)
            }
            showMessage(rv_user_list, "You're not admin!", Snackbar.LENGTH_SHORT)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        pauseItem = requireNotNull(menu?.let { it.findItem(R.id.pause) }) { "pauseItem null at $TAG" }
        leaveItem = requireNotNull(menu?.let { it.findItem(R.id.leave) }) { "leaveItem null at $TAG" }
        changeAdminItem = requireNotNull(menu?.let { it.findItem(R.id.changeAdmin) }) { "changeAdminItem null at $TAG" }
        currentUser?.let { setMenu(it) }
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
                gameViewModel.sendNotification(requestType, value, currentUser)
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
        val debt = requireNotNull(currentUser?.debt) { "debt was null at $TAG" }
        HelperDialog(this, onclick, getString(R.string.empty_deposit), RequestType.PAY, getString(R.string.pay_debt), getString(R.string.pay), debt, debt).show()
    }


    fun depositEndavans(view: View) {
        HelperDialog(this, onclick, getString(R.string.empty_deposit),
                RequestType.DEPOSIT, getString(R.string.deposit_endavans), getString(R.string.deposit), 1000, null).show()
    }

    fun withdrawEndavans(view: View) {
        val endavans = requireNotNull(currentUser?.endavans) { "endavans was null at $TAG" }
        HelperDialog(this, onclick, getString(R.string.empty_withdraw),
                RequestType.WITHDRAW, getString(R.string.withdraw_endavans), getString(R.string.withdraw), endavans, endavans).show()
    }
}
