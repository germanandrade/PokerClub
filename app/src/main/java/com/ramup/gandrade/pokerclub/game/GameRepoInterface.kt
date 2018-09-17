package com.ramup.gandrade.pokerclub.game

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.ramup.gandrade.pokerclub.model.Data
import com.ramup.gandrade.pokerclub.game.notifications.FCMResponse
import com.ramup.gandrade.pokerclub.game.notifications.RequestType
import com.ramup.gandrade.pokerclub.model.User

interface GameRepoInterface {


    val user: MutableLiveData<User?>

    val currentActiveGameId: MutableLiveData<String>
    var currentPausedGameId: MutableLiveData<String>
    val loggedIn: MutableLiveData<Boolean>

    val gameRef: CollectionReference

    val adminToken: MutableLiveData<String>

    val activeUsers: MutableLiveData<MutableMap<String, User>>


    fun checkPausedGames(): LiveData<String>

    fun checkActiveGames(): LiveData<String>

    fun createUserInGame(success: MutableLiveData<Boolean>)

    fun getCurrentGameId(): String

    fun getCurrentPausedGameId(): String


    fun joinUser(): MutableLiveData<Boolean>

    fun leave(success: MutableLiveData<Boolean>): LiveData<Boolean>

    fun createGame(): LiveData<String>

    fun pauseGame(): LiveData<Boolean>

    fun adminCount()
    fun resumeGame(): LiveData<Boolean>


    fun fetchUser(): LiveData<User?>


    fun getActiveUsers(): LiveData<MutableMap<String, User>>

    fun updateAdminToken(): LiveData<String>


    //--------------------------


    fun buyEndavans(uid: String): Task<Void>

    fun useLifeSaver(uid: String): Task<Void>?

    fun payDebt(uid: String, payValue: Int): Task<Void>

    fun depositEndavans(uid: String, valueToDeposit: Int): Task<Void>
    fun withdrawEndavans(uid: String, valueToWithDraw: Int): Task<Void>

    fun sendNotification(type: RequestType, extra: Int?, user: User?): io.reactivex.Observable<FCMResponse>
    fun sendSuccessNotification(data: Data): io.reactivex.Observable<FCMResponse>

    fun signOut()

    fun takeCareOfLogOut(): MutableLiveData<Boolean>

}