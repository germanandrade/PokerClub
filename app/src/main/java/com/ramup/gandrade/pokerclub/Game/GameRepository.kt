package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ramup.gandrade.pokerclub.Game.Game
import com.ramup.gandrade.pokerclub.Game.GameState

class GameRepository() {

    val auth = FirebaseAuth.getInstance()

    val user = MutableLiveData<User?>()

    val currentActiveGameId = MutableLiveData<String?>()
    val currentPausedGameId = MutableLiveData<String?>()

    val db = FirebaseFirestore.getInstance()
    val gameRef = db.collection("games")


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
        userDocument.set(User(auth.currentUser!!.displayName!!, 0, 0).toMap()).addOnSuccessListener {
            success.value = true
        }
    }

    fun getCurrentUser(): User? {
        return when
        {
            user==null->null
            else ->user.value
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
        doc.collection("users").document(auth.currentUser?.uid.toString()).set(User(auth.currentUser?.displayName
                ?: "err", 0, 0, admin = true).toMap())
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
            currentActiveGameId.value=currentPausedGameId.value
            currentPausedGameId.value=null
            userDocument.update("Admin", true,"Active",true).addOnSuccessListener {
                success.value = true }
        }
        return success
    }


    fun fetch(): LiveData<User?> {
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

    fun getActiveUsers(): LiveData<List<User>> {
        val activeUsers = MutableLiveData<List<User>>()

        val arr = ArrayList<User>(20)
        gameRef.document(currentActiveGameId.value
                ?: "0").collection("users").whereEqualTo("Active", true).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    if (document.exists()) {
                        val newUser = User(document.data)
                        arr.add(newUser)
                    }
                }
                activeUsers.value = arr

            }
        }
        return activeUsers
    }


    //--------------------------

    val docRef = db.collection("balance").document(auth.currentUser?.uid.toString())


    fun buyEndavans(): Task<Void> {
        var currentDebt = user.value?.debt ?: 0
        var newUser = user.value?.copy(debt = currentDebt + 500)
                ?: User(auth.currentUser?.email
                        ?: "none", 0, 500)
        user.value = newUser
        return docRef.set(newUser.toMap())
    }

    fun payDebt(): Task<Void> {
        var currentDebt = user.value?.debt ?: 0
        if (currentDebt == 0) {
            throw Exception("You have no debt")
        }
        var newUser = user.value?.copy(debt = 0)
                ?: User(auth.currentUser?.email
                        ?: "none", 0, 0)
        user.value = newUser
        return docRef.set(newUser.toMap())
    }

    fun depositEndavans(valueToDeposit: Int): Task<Void> {
        var currentEndavans = user.value?.endavans ?: 0
        var newUser = user.value?.copy(endavans = currentEndavans + valueToDeposit)
                ?: User(auth.currentUser?.email
                        ?: "none", valueToDeposit, 0)
        user.value = newUser
        return docRef.set(newUser.toMap())
    }

    fun withdrawEndavans(valueToWithDraw: Int): Task<Void> {
        var currentEndavans = user.value?.endavans ?: 0
        if (currentEndavans < valueToWithDraw) {
            throw Exception("Not enough Endavans")
        }
        var newUser = user.value?.copy(endavans = currentEndavans - valueToWithDraw)
                ?: User(auth.currentUser?.email
                        ?: "none", valueToWithDraw, 0)
        user.value = newUser
        return docRef.update(newUser.toMap())
    }
}


