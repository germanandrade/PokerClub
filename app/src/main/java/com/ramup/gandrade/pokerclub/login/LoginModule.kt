package com.ramup.gandrade.pokerclub.login

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.applicationContext

val loginModule = applicationContext {
    viewModel { LoginViewModel() }

}