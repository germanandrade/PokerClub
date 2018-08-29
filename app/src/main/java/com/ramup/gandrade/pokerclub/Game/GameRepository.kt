package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.ramup.gandrade.pokerclub.Game.*
import com.ramup.gandrade.pokerclub.Game.Notifications.*
import io.reactivex.Observable

class GameRepository(private val notificationApiService: NotificationApiService) {

    val auth = FirebaseAuth.getInstance()
    val TAG = GameRepository::class.java.simpleName

    val user = MutableLiveData<User?>()

    val currentActiveGameId = MutableLiveData<String?>()
    val currentPausedGameId = MutableLiveData<String?>()

    val db = FirebaseFirestore.getInstance()
    val gameRef = db.collection("games")

    val adminToken = MutableLiveData<String>()

    val activeUsers = MutableLiveData<MutableMap<String, User>>()


    fun checkPausedGames(): LiveData<String?> {
        gameRef.whereEqualTo("State", GameState.PAUSED.toString()).get().addOnCompleteListener { task ->
            if (task.isComplete) {
                for (doc in task.result) {
                    currentPausedGameId.value = doc.id
                }
                if (task.result.isEmpty) {
                    currentPausedGameId.value = null
                }
            }
        }
        return currentPausedGameId
    }

    fun checkActiveGames(): LiveData<String?> {
        gameRef.whereEqualTo("State", GameState.ACTIVE.toString()).get().addOnCompleteListener { task ->
            if (task.isComplete) {
                for (doc in task.result) {
                    currentActiveGameId.value = doc.id
                }
                if (task.result.isEmpty) {
                    currentActiveGameId.value = null
                }
            }
        }
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

    fun createGame(): LiveData<String?> {
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

    fun resumeGame(): LiveData<Boolean> {
        var success = MutableLiveData<Boolean>()
        var gameDocument = gameRef.document(getCurrentPausedGameId())
        var userDocument = gameDocument.collection("users").document(auth.currentUser!!.uid)
        gameDocument.update("State", GameState.ACTIVE.toString()).addOnSuccessListener {
            currentActiveGameId.value = currentPausedGameId.value
            currentPausedGameId.value = null
            userDocument.update("Admin", true, "Active", true, "Token", FirebaseInstanceId.getInstance().token).addOnSuccessListener {
                success.value = true
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
        gameRef.document(currentActiveGameId.value!!)
                .collection("users")
                .whereEqualTo("Active", true)
                .addSnapshotListener(EventListener { query, exception ->
                    if (exception != null) {
                        Log.d("fail", "fail")
                    } else {
                        val arr = mutableMapOf<String, User>()

                        for (document in query) {
                            if (document.exists()) {
                                val newUser = User(document.data)
                                arr.put(newUser.id, newUser)
                            }
                        }
                        activeUsers.value = arr
                    }
                })
        return activeUsers
    }

    fun updateAdminToken(): LiveData<String> {
        gameRef.document(currentActiveGameId.value!!)
                .collection("users")
                .whereEqualTo("Admin", true)
                .addSnapshotListener(EventListener { query, exception ->
                    if (exception != null) {
                        Log.d("fail", "fail")
                    } else {
                        for (document in query) {
                            if (document.exists()) {
                                adminToken.value = document.data["Token"] as String?
                                Log.d(TAG, "AdminToken:${adminToken.value}")
                            }
                        }
                    }
                })
        return adminToken
    }


    //--------------------------


    fun buyEndavans(uid: String): Task<Void> {
        var gameDocument = gameRef.document(getCurrentGameId())
        var userDocument = gameDocument.collection("users").document(uid)
        val current: User = activeUsers.value!!.get(uid)!!

        return userDocument.update("Debt", current.debt + 500)
    }

    fun payDebt(uid: String): Task<Void> {

        var gameDocument = gameRef.document(getCurrentGameId())
        var userDocument = gameDocument.collection("users").document(uid)
        val current: User = activeUsers.value!!.get(uid)!!
        var currentDebt = current.debt

        if (currentDebt == 0) {
            throw Exception("You have no debt")
        }
        return userDocument.update("Debt", 0)
    }

    fun depositEndavans(uid: String, valueToDeposit: Int): Task<Void> {
        var gameDocument = gameRef.document(getCurrentGameId())
        var userDocument = gameDocument.collection("users").document(uid)
        val current: User = activeUsers.value!!.get(uid)!!

        var currentEndavans = current.endavans

        return userDocument.update("Endavans", currentEndavans + valueToDeposit)
    }

    fun withdrawEndavans(uid: String, valueToWithDraw: Int): Task<Void> {
        var gameDocument = gameRef.document(getCurrentGameId())
        var userDocument = gameDocument.collection("users").document(uid)
        val current: User = activeUsers.value!!.get(uid)!!

        var currentEndavans = current.endavans
        if (currentEndavans < valueToWithDraw) {
            throw Exception("Not enough Endavans")
        }

        return userDocument.update("Endavans", currentEndavans - valueToWithDraw)
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


