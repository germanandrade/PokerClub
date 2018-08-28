package com.ramup.gandrade.pokerclub.Game

import com.ramup.gandrade.pokerclub.UserProfile.GameRepository
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val gameModule= applicationContext {
    viewModel {
       GameViewModel(get())
    }
    bean { GameRepository(get()) }
    bean { NotificationApiService.create()}
}