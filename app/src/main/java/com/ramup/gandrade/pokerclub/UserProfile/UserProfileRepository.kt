package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ramup.gandrade.pokerclub.Game.Game
import com.ramup.gandrade.pokerclub.Game.GameState

class UserProfileRepository() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("balance").document(auth.currentUser?.uid.toString())
    val gameRef = db.collection("games")

    val data = MutableLiveData<User>()
    val gameId = MutableLiveData<String>()


    fun fetch(): LiveData<User> {
        gameRef.document(gameId.value?:"0").collection("users").document(auth.currentUser?.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val newUser = User(document.data)
                    data.value = newUser
                }
            }
        }
        return data
    }

    fun buyEndavans(): Task<Void> {
        var currentDebt = data.value?.debt ?: 0
        var newUser = data.value?.copy(debt = currentDebt + 500)
                ?: User(auth.currentUser?.email
                        ?: "none", 0, 500)
        data.value = newUser
        return docRef.set(newUser.toMap())
    }

    fun payDebt(): Task<Void> {
        var currentDebt = data.value?.debt ?: 0
        if (currentDebt == 0) {
            throw Exception("You have no debt")
        }
        var newUser = data.value?.copy(debt = 0)
                ?: User(auth.currentUser?.email
                        ?: "none", 0, 0)
        data.value = newUser
        return docRef.set(newUser.toMap())
    }

    fun depositEndavans(valueToDeposit: Int): Task<Void> {
        var currentEndavans = data.value?.endavans ?: 0
        var newUser = data.value?.copy(endavans = currentEndavans + valueToDeposit)
                ?: User(auth.currentUser?.email
                        ?: "none", valueToDeposit, 0)
        data.value = newUser
        return docRef.set(newUser.toMap())
    }

    fun withdrawEndavans(valueToWithDraw: Int): Task<Void> {
        var currentEndavans = data.value?.endavans ?: 0
        if (currentEndavans < valueToWithDraw) {
            throw Exception("Not enough Endavans")
        }
        var newUser = data.value?.copy(endavans = currentEndavans - valueToWithDraw)
                ?: User(auth.currentUser?.email
                        ?: "none", valueToWithDraw, 0)
        data.value = newUser
        return docRef.update(newUser.toMap())
    }

    fun createGame(): LiveData<String> {
        val doc = gameRef.document()
        gameId.value = doc.id
        doc.set(Game().toMap())
        doc.collection("users").document(auth.currentUser?.uid.toString()).set(User(auth.currentUser?.displayName
                ?: "err", 0, 0,admin = true).toMap())
        return gameId
    }

    fun activateUserInGame(id: String): LiveData<String> {
        gameRef.document(id).collection("users").document(auth.currentUser?.uid.toString()).set(User(auth.currentUser?.displayName
                ?: "err", 0, 0).toMap())
        gameId.value = id
        return gameId
    }

    fun checkActiveGames(): LiveData<String?> {
        val activeGame = MutableLiveData<String?>()
        gameRef.whereEqualTo("State", GameState.ACTIVE.toString()).get().addOnCompleteListener { task ->
            if (task.isComplete) {
                for (doc in task.result){
                    activeGame.value = doc.id
                }
                if(task.result.isEmpty){
                    activeGame.value=null
                }
            }
        }
        return activeGame
    }

    fun checkPausedGame(): LiveData<String?> {
        val pausedGame = MutableLiveData<String?>()
        gameRef.whereEqualTo("State", GameState.PAUSED.toString()).get().addOnCompleteListener { task ->
            if (task.isComplete) {
                for (doc in task.result){
                    pausedGame.value = doc.id
                }
                if(task.result.isEmpty){
                    pausedGame.value=null
                }
            }
        }
        return pausedGame
    }



    fun getActiveUsers(): LiveData<List<User>> {
        val activeUsers = MutableLiveData<List<User>>()

        val arr = ArrayList<User>(20)
        gameRef.document(gameId.value?:"0").collection("users").whereEqualTo("Active",true).get().addOnCompleteListener { task ->
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

    fun pauseGame() {
        gameRef.document(gameId.value?:"0").update("STATE",GameState.PAUSED)
    }

    fun resumeGame() {

    }
}


