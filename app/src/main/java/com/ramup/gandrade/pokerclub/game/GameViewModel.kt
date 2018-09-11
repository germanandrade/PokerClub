package com.ramup.gandrade.pokerclub.userprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.game.notifications.RequestType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GameViewModel(val gameRepo: GameRepository) : ViewModel() {

    lateinit var pausedGameId: LiveData<String>

    lateinit var currentActiveGameId: LiveData<String>

    lateinit var successfulJoin: LiveData<Boolean>
    lateinit var successfulLeave: LiveData<Boolean>
    lateinit var successfulResume: LiveData<Boolean>

    lateinit var user: LiveData<User?>

    lateinit var adminToken: LiveData<String>


    fun getCurrentUser(): User? {
        return gameRepo.getCurrentUser()
    }


    fun checkActiveGames() {
        currentActiveGameId = gameRepo.checkActiveGames()
    }

    fun checkPausedGame() {
        pausedGameId = gameRepo.checkPausedGames()
    }

    fun createGame() {
        currentActiveGameId = gameRepo.createGame()
    }

    fun getCurrentGameId(): String {
        return gameRepo.getCurrentGameId()
    }

    fun joinUser() {
        successfulJoin = gameRepo.joinUser()
    }

    fun leave() {
        successfulLeave = gameRepo.leave()
    }

    fun pauseGame() {
        successfulLeave = gameRepo.pauseGame()
    }

    //----------------
    val mAuth = FirebaseAuth.getInstance();
    val loggedIn = MutableLiveData<Boolean>()
    lateinit var activeUsers: LiveData<MutableMap<String, User>>


    fun getUser() {
        user = gameRepo.fetchUser()
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

    fun checkActiveUsers() {
        activeUsers = gameRepo.getActiveUsers()
    }


    fun resumeGame() {
        successfulResume = gameRepo.resumeGame()
    }

    fun updateAdminToken() {
        try {

            adminToken = gameRepo.updateAdminToken()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    fun sendNotification(type: RequestType, extra: Int?,user:User?) {
        gameRepo.sendNotification(type, extra,user).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    Log.d("Result", "There are ${result} Java developers in Lagos")
                }, { error ->
                    error.printStackTrace()
                })
    }


}

