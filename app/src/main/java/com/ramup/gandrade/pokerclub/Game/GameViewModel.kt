package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.Game.Notifications.RequestType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GameViewModel(val gameRepo: GameRepository) : ViewModel() {

    lateinit var pausedGameId: LiveData<String?>

    lateinit var currentActiveGameId: LiveData<String?>

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
        adminToken = gameRepo.updateAdminToken()
    }

    fun sendNotification(type: RequestType, extra: Int?) {
        gameRepo.sendNotification(type, extra).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    Log.d("Result", "There are ${result} Java developers in Lagos")
                }, { error ->
                    error.printStackTrace()
                })
    }


}

