package com.ramup.gandrade.pokerclub.userprofile

import ACTIVE
import ADMIN
import DEBT
import ENDAVANS
import GAMES
import LIFESAVERS
import STATE
import TOKEN
import USERS
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.Keep
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.ramup.gandrade.pokerclub.MyTask
import com.ramup.gandrade.pokerclub.game.Game
import com.ramup.gandrade.pokerclub.game.GameState
import com.ramup.gandrade.pokerclub.game.notifications.*
import io.reactivex.Observable

@Keep
class GameRepository(private val notificationApiService: NotificationApiService) {

    private val auth = requireNotNull(FirebaseAuth.getInstance())
    private val TAG = GameRepository::class.java.simpleName
    private val user = MutableLiveData<User?>()
    private val currentActiveGameId = MutableLiveData<String>()
    private var currentPausedGameId = MutableLiveData<String>()
    private val db = FirebaseFirestore.getInstance()
    private val gameRef = db.collection(GAMES)
    private val adminToken = MutableLiveData<String>()
    private val activeUsers = MutableLiveData<MutableMap<String, User>>()

    fun checkPausedGames(): LiveData<String> {
        gameRef.whereEqualTo(STATE, GameState.PAUSED.toString())
                .addSnapshotListener { query, exception ->
                    if (exception != null) {
                        Log.d(TAG, exception.message)
                    } else {
                        if (!query.documents.isEmpty())
                            currentPausedGameId.value = query.documents[0].id
                    }
                }
        return currentPausedGameId
    }

    fun checkActiveGames(): LiveData<String> {
        gameRef.whereEqualTo(STATE, GameState.ACTIVE.toString())
                .addSnapshotListener { query, exception ->
                    if (exception != null) {
                        Log.d(TAG, exception.message)
                    } else {
                        if (!query.documents.isEmpty())
                            currentActiveGameId.value = query.documents[0].id
                    }
                }
        return currentActiveGameId
    }

    private fun getUserDocument(firebaseUser: FirebaseUser): DocumentReference {
        return gameRef.document(getCurrentGameId()).collection(USERS).document(firebaseUser.uid)
    }

    private fun getEmptyUser(firebaseUser: FirebaseUser): MutableMap<String, Any> {
        return User(requireNotNull(firebaseUser.displayName), firebaseUser.uid).toMap()
    }

    private fun createUserInGame(success: MutableLiveData<Boolean>) {
        val firebaseUser = requireNotNull(auth.currentUser)
        val userDocument = getUserDocument(firebaseUser)
        userDocument.set(getEmptyUser(firebaseUser)).addOnSuccessListener {
            success.value = true
        }
    }

    fun getCurrentGameId(): String {
        return requireNotNull(currentActiveGameId.value)
    }

    private fun getCurrentPausedGameId(): String {
        return requireNotNull(currentPausedGameId.value)
    }


    fun joinUser(): MutableLiveData<Boolean> {
        val success = MutableLiveData<Boolean>()
        val firebaseUser = requireNotNull(auth.currentUser)
        val userDocument = getUserDocument(firebaseUser)
        userDocument.update(ACTIVE, true)
                .addOnSuccessListener { success.value = true }
                .addOnFailureListener(OnFailureListener {
                    createUserInGame(success)
                })
        return success
    }

    fun leave(success: MutableLiveData<Boolean> = MutableLiveData()): LiveData<Boolean> {
        val firebaseUser = requireNotNull(auth.currentUser)
        val userDocument = getUserDocument(firebaseUser)
        userDocument.update("Active", false)
                .addOnSuccessListener {
                    success.value = true
                    user.value?.active = false
                }
        return success
    }

    fun createGame(): LiveData<String> {
        val doc = gameRef.document()
        currentActiveGameId.value = doc.id
        doc.set(Game().toMap())
        val firebaseUser = requireNotNull(auth.currentUser)
        val token = requireNotNull(FirebaseInstanceId.getInstance().token)
        val map = User(requireNotNull(firebaseUser.displayName), firebaseUser.uid, true).toMap()
        map.put(TOKEN, token)
        doc.collection(USERS).document(auth.currentUser?.uid.toString()).set(map)
        return currentActiveGameId
    }

    fun pauseGame(): LiveData<Boolean> {
        val firebaseUser = requireNotNull(auth.currentUser)
        var success = MutableLiveData<Boolean>()
        var gameDocument = gameRef.document(getCurrentGameId())
        var userDocument = getUserDocument(firebaseUser.uid)
        userDocument.update(ADMIN, false)
                .addOnSuccessListener {
                    gameDocument.update(STATE, GameState.PAUSED.toString()).addOnSuccessListener {
                        leave(success = success)
                    }
                }
        return success
    }


    fun resumeGame(): LiveData<Boolean> {
        val uid = requireNotNull(auth.currentUser).uid
        val success = MutableLiveData<Boolean>()
        val gameDocument = gameRef.document(getCurrentPausedGameId())
        val userDocument = getUserDocument(uid)
        val token = requireNotNull(FirebaseInstanceId.getInstance().token)
        getUserRef().whereEqualTo(ADMIN, true).get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.documents.count() > 0) {
                success.value = false
            } else {
                gameDocument.update(STATE, GameState.ACTIVE.toString()).addOnSuccessListener {_->
                    currentActiveGameId.value = currentPausedGameId.value
                    currentPausedGameId.value = null
                    userDocument.update(ADMIN, true, ACTIVE, true, TOKEN, FirebaseInstanceId.getInstance().token)
                            .addOnSuccessListener {
                                success.value = true
                            }
                            .addOnFailureListener { it ->
                                Log.d(TAG, it.message)
                                val firebaseUser = requireNotNull(auth.currentUser)
                                val map = User(requireNotNull(firebaseUser.displayName), 0, 0, firebaseUser.uid, admin = true).toMap()
                                map.put(TOKEN, token)
                                userDocument.set(map).addOnSuccessListener {
                                    success.value = true
                                }
                            }
                }
            }
        }

        return success
    }


    fun fetchUser(): LiveData<User?> {
        val firebaseUser = requireNotNull(auth.currentUser)
        val userDocument = getUserDocument(firebaseUser)
        userDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val newUser = User(document.data)
                    user.value = newUser
                } else {
                    user.value = null
                }
            }
        }
        return user
    }

    /**
     *
    fun getActiveUsers(): LiveData<MutableMap<String, User>> {
        currentActiveGameId.value?.let { pCurrentActiveGameId ->
            {
                gameRef.document(pCurrentActiveGameId)
                        .collection("users")
                        .whereEqualTo("Active", true)
                        .addSnapshotListener { query, exception ->
                            if (exception != null) {
                                Log.d("fail", exception.message)
                            } else {
                                val arr: MutableMap<String, User> = query.documents.filter { it.exists() }.associateTo(mutableMapOf<String, User>())
                                {
                                    val u = User(it.data)
                                    u.id to u
                                }
                                activeUsers.value = arr
                                val uid = requireNotNull(auth.currentUser).uid
                                user.value = arr[uid]
                            }
                        }
            }
        }
        return activeUsers
    }
     */

    fun getActiveUsers(): LiveData<MutableMap<String, User>> {
        if (currentActiveGameId.value != null) gameRef.document(currentActiveGameId.value!!)
                .collection("users")
                .whereEqualTo("Active", true)
                .addSnapshotListener { query, exception ->
                    if (exception != null) {
                        Log.d("fail", exception.message)
                    } else {
                        val arr: MutableMap<String, User> = query.documents.filter { it.exists() }.associateTo(mutableMapOf<String, User>()) {
                            val u = User(it.data)
                            u.id to u
                        }
                        activeUsers.value = arr
                        user.value = arr[auth.currentUser!!.uid]
                    }
                }

        return activeUsers
    }


    fun updateAdminToken(): LiveData<String> {
        try {
            currentActiveGameId.value?.let {
                gameRef.document(requireNotNull(it))
                        .collection(USERS)
                        .whereEqualTo(ADMIN, true)
                        .addSnapshotListener { query, exception ->
                            if (exception != null) {
                                Log.d(TAG, exception.message)
                            } else {
                                if (!query.documents.isEmpty()) {
                                    adminToken.value = query.documents[0].data[TOKEN] as String?
                                    Log.d(TAG, "AdminToken:${adminToken.value}")
                                }
                            }
                        }
                return adminToken
            }
        } catch (e: Exception) {
            throw Exception("${e.message} Game isn't active")
        }
        return adminToken
    }


    fun buyEndavans(uid: String): Task<Void> {
        try {
            val userDocument = getUserDocument(uid)
            val current = getCurrentUser(uid)
            return userDocument.update(DEBT, current.debt + 500)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun useLifeSaver(uid: String): Task<Void>? {
        try {
            var userDocument = getUserDocument(uid)
            val current = getCurrentUser(uid)

            return userDocument.update(LIFESAVERS, current.lifeSavers - 1)
        } catch (e: Exception) {
            return MyTask()
        }
    }

    fun payDebt(uid: String, payValue: Int): Task<Void> {
        try {
            var userDocument = getUserDocument(uid)
            val current = getCurrentUser(uid)

            var currentDebt = current.debt

            if (payValue > currentDebt) {
                throw Exception("You can't pay more")
            }
            return userDocument.update(DEBT, currentDebt - payValue)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun depositEndavans(uid: String, valueToDeposit: Int): Task<Void> {
        try {
            var userDocument = getUserDocument(uid)
            Log.d("Game", "${activeUsers.value} uid: $uid")
            val current = getCurrentUser(uid)

            var currentEndavans = current.endavans
            return userDocument.update(ENDAVANS, currentEndavans + valueToDeposit)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun getCurrentUser(uid: String): User {
        val activeUsersValue = requireNotNull(activeUsers.value)
        return requireNotNull(activeUsersValue.get(uid))
    }

    private fun getUserRef(): CollectionReference {
        var gameDocument = gameRef.document(getCurrentGameId())
        return gameDocument.collection(USERS)
    }

    private fun getUserDocument(uid: String): DocumentReference {
        return getUserRef().document(uid)
    }

    fun withdrawEndavans(uid: String, valueToWithDraw: Int): Task<Void> {
        try {
            var userDocument = getUserDocument(uid)

            val current = getCurrentUser(uid)
            var currentEndavans = current.endavans
            if (currentEndavans < valueToWithDraw) {
                throw Exception("Not enough Endavans")
            }

            return userDocument.update(ENDAVANS, currentEndavans - valueToWithDraw)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun sendNotification(type: RequestType, extra: Int?, user: User?): Observable<FCMResponse> {
        val firebaseUser = requireNotNull(auth.currentUser)
        val tokenAdmin = requireNotNull(adminToken.value)
        val newToken = requireNotNull(FirebaseInstanceId.getInstance().token)
        val token = if (user != null && user.admin) tokenAdmin else newToken
        val data = Data(requireNotNull(firebaseUser.displayName), firebaseUser.uid, token, type.toString(), extra)
        val request = Request(tokenAdmin, data)
        return notificationApiService.sendNotification(request)
    }

    fun sendSuccessNotification(data: Data): Observable<FCMResponse> {
        val request = Request(data.token, data)
        return notificationApiService.sendNotification(request)
    }


}


