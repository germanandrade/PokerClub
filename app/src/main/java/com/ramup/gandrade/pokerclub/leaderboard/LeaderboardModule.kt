package com.ramup.gandrade.pokerclub.leaderboard

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.applicationContext

val globalModule = applicationContext {
    viewModel {
        LeaderboardViewModel(get())
    }
    bean { LeaderboardRepository() }
}