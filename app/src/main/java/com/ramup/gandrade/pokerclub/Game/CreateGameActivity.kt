package com.ramup.gandrade.pokerclub.Game

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.example.gandrade.pokerclub.util.TextToImageEncode
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import kotlinx.android.synthetic.main.activity_create_game.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel

class CreateGameActivity : FragmentActivity() {
    val userProfileViewModel by viewModel<GameViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        userProfileViewModel.createGame()
        userProfileViewModel.gameId?.observe(this, Observer {
            id ->
            showMessage(image,"id:$id")
            if (id != null) {
                generate(id)
            }
        })


    }

    private fun generate(id: String) {
        val bitmap = TextToImageEncode(id)
        image.setImageBitmap(bitmap)
    }
    fun startGame(view: View){
        startActivity<GameActivity>()
    }

}
