package com.ramup.gandrade.pokerclub.userprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.Keep
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.ramup.gandrade.pokerclub.MyTask
import com.ramup.gandrade.pokerclub.game.GameRepoInterface
import com.ramup.gandrade.pokerclub.game.notifications.*
import com.ramup.gandrade.pokerclub.model.*
import io.reactivex.Observable

@Keep
class GameRepository(private val notificationApiService: NotificationApiService, private val auth: FirebaseAuth, private val dB: FirebaseFirestore) : GameRepoInterface {


    val TAG = GameRepository::class.java.simpleName

    override val user = MutableLiveData<User?>()

    override val currentActiveGameId = MutableLiveData<String>()
    override var currentPausedGameId = MutableLiveData<String>()

    override val gameRef = dB.collection("games")

    override val adminToken = MutableLiveData<String>()

    override val activeUsers = MutableLiveData<MutableMap<String, User>>()


    override fun checkPausedGames(): LiveData<String> {
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

    override fun checkActiveGames(): LiveData<String> {
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

    override fun createUserInGame(success: MutableLiveData<Boolean>) {
        var userDocument = gameRef.document(getCurrentGameId()).collection("users").document(auth.currentUser!!.uid)
        userDocument.set(User(auth.currentUser!!.displayName!!, 0, 0, auth.currentUser!!.uid).toMap()).addOnSuccessListener {
            success.value = true
        }
    }

    override fun getCurrentGameId(): String {
        return currentActiveGameId.value!!
    }

    override fun getCurrentPausedGameId(): String {
        return currentPausedGameId.value!!
    }


    override fun joinUser(): MutableLiveData<Boolean> {
        var success = MutableLiveData<Boolean>()
        var userDocument = gameRef.document(getCurrentGameId()).collection("users").document(auth.currentUser!!.uid)
        userDocument.update("Active", true)
                .addOnSuccessListener { success.value = true }
                .addOnFailureListener(OnFailureListener {
                    createUserInGame(success)
                })
        return success
    }

    override fun leave(success: MutableLiveData<Boolean>): LiveData<Boolean> {
        var userDocument = gameRef.document(getCurrentGameId()).collection("users").document(auth.currentUser!!.uid)
        userDocument.update("Active", false)
                .addOnSuccessListener {
                    success.value = true
                    user.value?.active = false
                }
        return success
    }

    override fun createGame(): LiveData<String> {
        val doc = gameRef.document()
        currentActiveGameId.value = doc.id
        doc.set(Game().toMap())
        val map = User(auth.currentUser?.displayName!!, 0, 0, auth.currentUser!!.uid, admin = true).toMap()
        map.put("Token", FirebaseInstanceId.getInstance().token!!)
        doc.collection("users").document(auth.currentUser?.uid.toString()).set(map)
        return currentActiveGameId
    }

    override fun pauseGame(): LiveData<Boolean> {

        var success = MutableLiveData<Boolean>()
        var gameDocument = gameRef.document(getCurrentGameId())
        var userDocument = gameDocument.collection("users").document(auth.currentUser!!.uid)

        userDocument.update("Admin", false)
                .addOnSuccessListener {
                    gameDocument.update("State", GameState.PAUSED.toString()).addOnSuccessListener {
                        leave(success)
                    }
                }
        return success
    }

    override fun adminCount() {
        var gameDocument = gameRef.document(getCurrentPausedGameId())

    }

    override fun resumeGame(): LiveData<Boolean> {
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


    override fun fetchUser(): LiveData<User?> {
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


    override fun getActiveUsers(): LiveData<MutableMap<String, User>> {
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

    override fun updateAdminToken(): LiveData<String> {
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


    override fun buyEndavans(uid: String): Task<Void> {
        try {
            var gameDocument = gameRef.document(getCurrentGameId())
            var userDocument = gameDocument.collection("users").document(uid)
            val current: User = activeUsers.value!!.get(uid)!!
            return userDocument.update("Debt", current.debt + 500)
        } catch (e: Exception) {
            return MyTask()
        }

    }

    override fun useLifeSaver(uid: String): Task<Void>? {
        try {
            var gameDocument = gameRef.document(getCurrentGameId())
            var userDocument = gameDocument.collection("users").document(uid)
            val current: User = activeUsers.value!!.get(uid)!!
            return userDocument.update("LifeSavers", current.lifeSavers - 1)
        } catch (e: Exception) {
            return MyTask()
        }
    }

    override fun payDebt(uid: String, payValue: Int): Task<Void> {
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

    override fun depositEndavans(uid: String, valueToDeposit: Int): Task<Void> {
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

    override fun withdrawEndavans(uid: String, valueToWithDraw: Int): Task<Void> {
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

    override fun sendNotification(type: RequestType, extra: Int?, user: User?): Observable<FCMResponse> {
        val token = if (user != null && user.admin) adminToken.value!! else FirebaseInstanceId.getInstance().token!!
        val data = Data(auth.currentUser!!.displayName!!, auth.currentUser!!.uid, token, type.toString(), extra)
        val request = Request(adminToken.value!!, data)
        return notificationApiService.sendNotification(request)
    }

    override fun sendSuccessNotification(data: Data): Observable<FCMResponse> {
        val request = Request(data.token, data)
        return notificationApiService.sendNotification(request)
    }

    override val loggedIn = MutableLiveData<Boolean>()

    override fun signOut() {
        auth.signOut()
    }

    override fun takeCareOfLogOut(): MutableLiveData<Boolean> {
        auth.addAuthStateListener {
            if (auth.currentUser == null) {
                loggedIn.value = false
            }
        }
        return loggedIn

    }


}


