package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.Game.GameState

class GameViewModel(val gameRepo: GameRepository) : ViewModel() {

    lateinit var activeGameId: LiveData<String?>
    lateinit var pausedGameId: LiveData<String?>

    lateinit var gameId: LiveData<String>

    fun checkActiveGames() {
        activeGameId = gameRepo.checkGames(GameState.ACTIVE)
    }

    fun checkPausedGame() {
        pausedGameId = gameRepo.checkGames(GameState.PAUSED)
    }

    fun createGame() {
        gameId = gameRepo.createGame()
    }

    fun getCurrentGameId(): String {
        return gameRepo.getCurrentGameId()
    }

    //----------------
    val mAuth = FirebaseAuth.getInstance();
    lateinit var user: LiveData<User>
    val loggedIn = MutableLiveData<Boolean>()
    lateinit var activeUsers: LiveData<List<User>>


    fun getUser() {
        user = gameRepo.fetch()
    }

    fun buyEndavans(): Task<Void> {
        return gameRepo.buyEndavans()
    }

    fun payDebt(): Task<Void> {
        return gameRepo.payDebt()
    }

    fun depositEndavans(valueToDeposit: Int): Task<Void> {
        return gameRepo.depositEndavans(valueToDeposit)
    }

    fun withdrawEndavans(valueToWithdraw: Int): Task<Void> {
        return gameRepo.withdrawEndavans(valueToWithdraw)

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


    fun activateUserInGame(id: String) {
        gameId = gameRepo.activateUserInGame(id)
    }


    fun checkActiveUsers() {
        activeUsers = gameRepo.getActiveUsers()
    }

    fun pauseGame() {
        gameRepo.pauseGame()
    }

    fun resumeGame() {
        gameRepo.resumeGame()
    }




}

