package com.ramup.gandrade.pokerclub

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class UserProfileViewModel(val userRepo: UserProfileRepository) : ViewModel() {
    val mAuth = FirebaseAuth.getInstance();
    var user: LiveData<User>
    val loggedIn = MutableLiveData<Boolean>()

    fun buyEndavans(): Task<Void> {
        return userRepo.buyEndavans()
    }

    fun payDebt(): Task<Void> {
        return userRepo.payDebt()
    }

    fun depositEndavans(valueToDeposit: Int): Task<Void> {
        return userRepo.depositEndavans(valueToDeposit)
    }

    fun withdrawEndavans(valueToWithdraw: Int): Task<Void> {
        return userRepo.withdrawEndavans(valueToWithdraw)

    }

    fun signOut() {

        mAuth.addAuthStateListener {
            if (mAuth.currentUser == null) {
                loggedIn.value = false
            }
        }
        mAuth.signOut()
    }

    fun getName(): String {
        return mAuth.currentUser?.email.toString()
    }


    init {
        user = userRepo.fetch()
    }

}

