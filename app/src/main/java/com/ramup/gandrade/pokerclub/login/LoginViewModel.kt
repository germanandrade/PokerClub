package com.ramup.gandrade.pokerclub.login

import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class LoginViewModel() : ViewModel() {


    val mAuth = FirebaseAuth.getInstance();

    fun sigIn(email: String, password: String): Task<AuthResult> {
        return mAuth.signInWithEmailAndPassword(email, password)
    }

    fun signUp(email: String, password: String): Task<AuthResult> {
        return mAuth.createUserWithEmailAndPassword(email, password)
    }

    fun isLogged(): Boolean {
        return mAuth.currentUser != null
    }

    fun setDisplayName(newDisplayName: String) {
        val user = mAuth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(newDisplayName).build()
        user?.updateProfile(profileUpdates)
    }
}
