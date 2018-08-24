package com.ramup.gandrade.pokerclub.Game

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
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserAdapter
import com.ramup.gandrade.pokerclub.UserProfile.User
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.image_dialog.view.*
import org.koin.android.architecture.ext.viewModel

class GameActivity : FragmentActivity() {
    val gameViewModel by viewModel<GameViewModel>()
    lateinit var pauseItem:MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        if (intent.extras != null) {
            val gameId = intent.getStringExtra("id")
            gameViewModel.activateUserInGame(gameId)
        }
        rv_user_list.layoutManager = LinearLayoutManager(this)
        rv_user_list.adapter = UserAdapter(listOf<User>(), this)

        gameViewModel.checkActiveUsers()
        gameViewModel.activeUsers.observe(this, Observer { list ->
            rv_user_list.adapter = UserAdapter(list!!, this)

        })
        gameViewModel.getUser()
        gameViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                if (user.admin)
                    showMessage(rv_user_list,"You're admin!")
                pauseItem.setVisible(true)
            } else {
                showMessage(rv_user_list, "You're null!")
            }

        })
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        pauseItem= menu!!.findItem(R.id.pause)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.showqr -> showQr()
            R.id.pause -> pause()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pause() {
        gameViewModel.pauseGame()
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
        view.dialog_imageview.setImageBitmap(TextToImageEncode(gameViewModel.gameId?.value
                ?: "0"))
        builder.setView(view)
        builder.setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
            // continue with delete
        }).show()
    }

}
