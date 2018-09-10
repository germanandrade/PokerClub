package com.ramup.gandrade.pokerclub.leaderboard

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ramup.gandrade.pokerclub.userprofile.User


class LeaderboardRepository() {
    val db = FirebaseFirestore.getInstance()
    val gameRef = db.collection("games")
    val data = MutableLiveData<MutableMap<String, User>>()
    val activeUsers = MutableLiveData<MutableMap<String, User>>()

    private val currentGameId = MutableLiveData<String>()


    fun fetchUsers(): LiveData<MutableMap<String, User>> {
        gameRef.document(currentGameId.value!!)
                .collection("users")
                .orderBy("LifeSavers", Query.Direction.DESCENDING)
                .orderBy("Endavans", Query.Direction.DESCENDING)
                .orderBy("Debt", Query.Direction.ASCENDING)
                .get().addOnSuccessListener { query ->
                    val arr: MutableMap<String, User> = query.documents.filter { it.exists() }.associateTo(mutableMapOf<String, User>()) {
                        val u = User(it.data)
                        u.id to u
                    }
                    activeUsers.value = arr
                }
                .addOnFailureListener {
                    Log.d("A", it.message)
                }
        return activeUsers
    }


    fun checkCurrentGameId(): LiveData<String?> {
        gameRef.get().addOnCompleteListener { task ->
            if (!task.result.documents.isEmpty())
                currentGameId.value = task.result.documents[0].id
        }
        return currentGameId
    }


}
