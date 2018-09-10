package com.ramup.gandrade.pokerclub.game

import com.ramup.gandrade.pokerclub.game.notifications.NotificationApiService
import com.ramup.gandrade.pokerclub.userprofile.GameRepository
import com.ramup.gandrade.pokerclub.userprofile.GameViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val gameModule = applicationContext {
    viewModel {
        GameViewModel(get())
    }
    bean { GameRepository(get()) }
    bean { NotificationApiService.create() }
    bean{NotificationCounter()}
}