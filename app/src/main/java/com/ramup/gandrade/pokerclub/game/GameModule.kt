package com.ramup.gandrade.pokerclub.game

import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.game.notifications.NotificationApiService
import com.ramup.gandrade.pokerclub.userprofile.GameRepository
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val gameModule = module {
    viewModel {
        GameViewModel(get())
    }
    single { GameRepository(get(), get()) }
    single { NotificationApiService.create() }
    single { NotificationCounter() }
    single { FirebaseAuth.getInstance() }
}