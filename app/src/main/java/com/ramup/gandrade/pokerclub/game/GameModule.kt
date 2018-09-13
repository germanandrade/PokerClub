package com.ramup.gandrade.pokerclub.game

import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.game.notifications.NotificationApiService
import com.ramup.gandrade.pokerclub.userprofile.GameRepository
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val gameModule = applicationContext {
    viewModel {
        GameViewModel(get())
    }
    bean { GameRepository(get(),get()) }
    bean { NotificationApiService.create() }
    bean { NotificationCounter() }
    bean{ FirebaseAuth.getInstance()}
}