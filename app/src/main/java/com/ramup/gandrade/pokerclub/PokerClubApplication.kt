package com.ramup.gandrade.pokerclub

import android.app.Application
import com.ramup.gandrade.pokerclub.Login.loginModule
import org.koin.android.ext.android.startKoin

class PokerClubApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(loginModule, userProfileModule))
    }
}