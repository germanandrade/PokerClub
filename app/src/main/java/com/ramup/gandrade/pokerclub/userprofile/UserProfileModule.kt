package com.ramup.gandrade.pokerclub.userprofile

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.applicationContext

val userProfileModule = applicationContext {
    viewModel {
        UserProfileViewModel(get())
    }
    bean { UserProfileRepository() }
}