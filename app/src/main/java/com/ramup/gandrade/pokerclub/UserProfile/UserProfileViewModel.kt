package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.Game.GameState

class UserProfileViewModel(val userRepo: UserProfileRepository) : ViewModel() {
    val mAuth = FirebaseAuth.getInstance();
    lateinit var user: LiveData<User>
    var gameId: LiveData<String>?=null
    lateinit var pausedGameId: LiveData<String?>
    var activeGameId: LiveData<String?>?=null
    val loggedIn = MutableLiveData<Boolean>()
    lateinit var activeUsers: LiveData<List<User>>


    fun getUser() {
        user = userRepo.fetch()
    }

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

    fun createGame() {
        gameId = userRepo.createGame()
    }

    fun activateUserInGame(id: String) {
        gameId = userRepo.activateUserInGame(id)
    }

    fun checkPausedGame() {
        pausedGameId = userRepo.checkPausedGame()

    }
    fun checkActiveGames() {
        activeGameId = userRepo.checkActiveGames()
    }

    fun checkActiveUsers() {
        activeUsers = userRepo.getActiveUsers()
    }

    fun pauseGame() {
        userRepo.pauseGame()
    }

    fun resumeGame() {
        userRepo.resumeGame()
    }


}

