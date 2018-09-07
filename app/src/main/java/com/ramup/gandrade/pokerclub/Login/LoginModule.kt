package com.ramup.gandrade.pokerclub.Login

import com.google.firebase.auth.FirebaseAuth
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val loginModule= applicationContext {
    viewModel { LoginViewModel() }

}