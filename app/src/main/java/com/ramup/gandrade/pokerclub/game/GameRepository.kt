package com.ramup.gandrade.pokerclub.userprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.ramup.gandrade.pokerclub.MyTask
import com.ramup.gandrade.pokerclub.game.Game
import com.ramup.gandrade.pokerclub.game.GameState
import com.ramup.gandrade.pokerclub.game.notifications.*
import io.reactivex.Observable


class GameRepository(private val notificationApiService: NotificationApiService) {

    val auth = FirebaseAuth.getInstance()
    val TAG = GameRepository::class.java.simpleName

    val user = MutableLiveData<User?>()

    val currentActiveGameId = MutableLiveData<String>()
    var currentPausedGameId = MutableLiveData<String>()

    val db = FirebaseFirestore.getInstance()
    val gameRef = db.collection("games")

    val adminToken = MutableLiveData<String>()

    val activeUsers = MutableLiveData<MutableMap<String, User>>()


    fun checkPausedGames(): LiveData<String> {
        gameRef.whereEqualTo("State", GameState.PAUSED.toString())
                .addSnapshotListener(EventListener { query, exception ->
                    if (exception != null) {
                        Log.d("fail", "fail")
                    } else {
                        if (!query.documents.isEmpty())
                            currentPausedGameId.value = query.documents[0].id

                    }
                })
        return currentPausedGameId
    }

    fun checkActiveGames(): LiveData<String> {
        gameRef.whereEqualTo("State", GameState.ACTIVE.toString())
                .addSnapshotListener(EventListener { query, exception ->
                    if (exception != null) {
                        Log.d("fail", "fail")
                    } else {
                        if (!query.documents.isEmpty())
                            currentActiveGameId.value = query.documents[0].id
                    }
                })
        return currentActiveGameId
    }

    fun createUserInGame(success: MutableLiveData<Boolean>) {
        var userDocument = gameRef.document(getCurrentGameId()).collection("users").document(auth.currentUser!!.uid)
        userDocument.set(User(auth.currentUser!!.displayName!!, 0, 0, auth.currentUser!!.uid).toMap()).addOnSuccessListener {
            success.value = true
        }
    }

    fun getCurrentUser(): User? {
        return when {
            user == null -> null
            else -> user.value
        }

    }

    fun getCurrentGameId(): String {
        return currentActiveGameId.value!!
    }

    fun getCurrentPausedGameId(): String {
        return currentPausedGameId.value!!
    }


    fun joinUser(): MutableLiveData<Boolean> {
        var success = MutableLiveData<Boolean>()
        var userDocument = gameRef.document(getCurrentGameId()).collection("users").document(auth.currentUser!!.uid)
        userDocument.update("Active", true)
                .addOnSuccessListener { success.value = true }
                .addOnFailureListener(OnFailureListener {
                    createUserInGame(success)
                })
        return success
    }

    fun leave(success: MutableLiveData<Boolean> = MutableLiveData()): LiveData<Boolean> {
        var userDocument = gameRef.document(getCurrentGameId()).collection("users").document(auth.currentUser!!.uid)
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
        val map = User(auth.currentUser?.displayName!!, 0, 0, auth.currentUser!!.uid, admin = true).toMap()
        map.put("Token", FirebaseInstanceId.getInstance().token!!)
        doc.collection("users").document(auth.currentUser?.uid.toString()).set(map)
        return currentActiveGameId
    }

    fun pauseGame(): LiveData<Boolean> {

        var success = MutableLiveData<Boolean>()
        var gameDocument = gameRef.document(getCurrentGameId())
        var userDocument = gameDocument.collection("users").document(auth.currentUser!!.uid)

        userDocument.update("Admin", false)
                .addOnSuccessListener {
                    gameDocument.update("State", GameState.PAUSED.toString()).addOnSuccessListener {
                        leave(success = success)
                    }
                }
        return success
    }

    fun adminCount() {
        var gameDocument = gameRef.document(getCurrentPausedGameId())

    }

    fun resumeGame(): LiveData<Boolean> {
        var success = MutableLiveData<Boolean>()
        var gameDocument = gameRef.document(getCurrentPausedGameId())
        var userDocument = gameDocument.collection("users").document(auth.currentUser!!.uid)
        gameDocument.collection("users").whereEqualTo("Admin", true).get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.documents.count() > 0) {
                success.value = false
            } else {
                gameDocument.update("State", GameState.ACTIVE.toString()).addOnSuccessListener {
                    currentActiveGameId.value = currentPausedGameId.value
                    currentPausedGameId.value = null
                    userDocument.update("Admin", true, "Active", true, "Token", FirebaseInstanceId.getInstance().token)
                            .addOnSuccessListener {
                                success.value = true
                            }
                            .addOnFailureListener { it ->
                                Log.d("ex", "Exception")
                                val map = User(auth.currentUser?.displayName!!, 0, 0, auth.currentUser!!.uid, admin = true).toMap()
                                map.put("Token", FirebaseInstanceId.getInstance().token!!)
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
        var userDocument = gameRef.document(getCurrentGameId()).collection("users").document(auth.currentUser!!.uid)
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
            if (currentActiveGameId.value != null) {

                gameRef.document(currentActiveGameId.value!!)
                        .collection("users")
                        .whereEqualTo("Admin", true)
                        .addSnapshotListener { query, exception ->
                            if (exception != null) {
                                Log.d("fail", "fail")
                            } else {
                                if (!query.documents.isEmpty()) {
                                    adminToken.value = query.documents[0].data["Token"] as String?
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


    //--------------------------


    fun buyEndavans(uid: String): Task<Void> {
        try {
            var gameDocument = gameRef.document(getCurrentGameId())
            var userDocument = gameDocument.collection("users").document(uid)
            val current: User = activeUsers.value!!.get(uid)!!
            return userDocument.update("Debt", current.debt + 500)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun useLifeSaver(uid: String): Task<Void>? {
        try {
            var gameDocument = gameRef.document(getCurrentGameId())
            var userDocument = gameDocument.collection("users").document(uid)
            val current: User = activeUsers.value!!.get(uid)!!
            return userDocument.update("LifeSavers", current.lifeSavers - 1)
        } catch (e: Exception) {
            return MyTask()
        }
    }

    fun payDebt(uid: String, payValue: Int): Task<Void> {
        try {
            var gameDocument = gameRef.document(getCurrentGameId())
            var userDocument = gameDocument.collection("users").document(uid)
            val current: User = activeUsers.value!!.get(uid)!!
            var currentDebt = current.debt

            if (payValue > currentDebt) {
                throw Exception("You can't pay more")
            }
            return userDocument.update("Debt", currentDebt - payValue)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun depositEndavans(uid: String, valueToDeposit: Int): Task<Void> {
        try {
            var gameDocument = gameRef.document(getCurrentGameId())
            var userDocument = gameDocument.collection("users").document(uid)
            Log.d("Game", "${activeUsers.value} uid: $uid")
            val current: User = activeUsers.value!!.get(uid)!!
            var currentEndavans = current.endavans
            return userDocument.update("Endavans", currentEndavans + valueToDeposit)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun withdrawEndavans(uid: String, valueToWithDraw: Int): Task<Void> {
        try {
            var gameDocument = gameRef.document(getCurrentGameId())
            var userDocument = gameDocument.collection("users").document(uid)

            val current: User = activeUsers.value!!.get(uid)!!
            var currentEndavans = current.endavans
            if (currentEndavans < valueToWithDraw) {
                throw Exception("Not enough Endavans")
            }

            return userDocument.update("Endavans", currentEndavans - valueToWithDraw)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    fun sendNotification(type: RequestType, extra: Int?): Observable<FCMResponse> {
        val data = Data(auth.currentUser!!.displayName!!, auth.currentUser!!.uid, FirebaseInstanceId.getInstance().token!!, type.toString(), extra)
        val request = Request(adminToken.value!!, data)
        return notificationApiService.sendNotification(request)
    }

    fun sendSuccessNotification(data: Data): Observable<FCMResponse> {
        val request = Request(data.token, data)
        return notificationApiService.sendNotification(request)
    }


}


