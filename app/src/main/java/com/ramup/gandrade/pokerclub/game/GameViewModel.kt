package com.ramup.gandrade.pokerclub.game

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.Keep
import android.util.Log
import com.ramup.gandrade.pokerclub.game.notifications.RequestType
import com.ramup.gandrade.pokerclub.userprofile.GameRepository
import com.ramup.gandrade.pokerclub.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Keep
class GameViewModel(val gameRepo: GameRepository) : ViewModel() {

    lateinit var pausedGameId: LiveData<String>

    lateinit var currentActiveGameId: LiveData<String>

    lateinit var successfulJoin: LiveData<Boolean>
    lateinit var successfulLeave: LiveData<Boolean>
    lateinit var successfulResume: LiveData<Boolean>

    lateinit var user: LiveData<User?>

    lateinit var adminToken: LiveData<String>

    private val TAG = GameViewModel::class.simpleName




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
        successfulLeave = gameRepo.leave(MutableLiveData<Boolean>())
    }

    fun pauseGame() {
        successfulLeave = gameRepo.pauseGame()
    }

    //----------------
//    val mAuth = FirebaseAuth.getInstance();
    lateinit var loggedIn: MutableLiveData<Boolean>
    lateinit var activeUsers: LiveData<MutableMap<String, User>>


    fun getUser() {
        user = gameRepo.fetchUser()
    }

    fun signOut() {
        gameRepo.signOut()
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

    fun sendNotification(type: RequestType, extra: Int?, user: User?) {
        gameRepo.sendNotification(type, extra, user).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    Log.d(TAG, "Result is ${result}")
                }, { error ->
                    error.printStackTrace()
                })
    }

    fun takeCareOfLogOut() {
        loggedIn = gameRepo.takeCareOfLogOut()
    }


}

