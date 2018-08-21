package com.ramup.gandrade.pokerclub

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val userProfileModule= applicationContext {
    viewModel { UserProfileViewModel(get())
    }
    bean { UserProfileRepository() }
}