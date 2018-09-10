package com.ramup.gandrade.pokerclub

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.ramup.gandrade.pokerclub.game.gameModule
import com.ramup.gandrade.pokerclub.leaderboard.globalModule
import com.ramup.gandrade.pokerclub.login.loginModule
import com.ramup.gandrade.pokerclub.userprofile.userProfileModule
import org.koin.android.ext.android.startKoin

class PokerClubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("PokerClubApplication","onCreate")
        startKoin(this, listOf(gameModule, loginModule, userProfileModule, globalModule))
        FirebaseApp.initializeApp(this);
        //Init sharedPreferences
        //SharedPreferences
    }
}