package com.ramup.gandrade.pokerclub.leaderboard

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val globalModule = applicationContext {
    viewModel {
        LeaderboardViewModel(get())
    }
    bean { LeaderboardRepository() }
}