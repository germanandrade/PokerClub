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
                .orderBy("Endavans", Query.Direction.DESCENDING)
                .orderBy("Debt", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { query ->
                    val arr = mutableMapOf<String, User>()
                    for (document in query) {
                        if (document.exists()) {
                            val newUser = User(document.data)
                            arr.put(newUser.id, newUser)
                        }
                    }
                    activeUsers.value = arr
                }
                .addOnFailureListener {
                    Log.d("A", "a")
                }
        /*
        .addSnapshotListener(EventListener { query, exception ->
            if (exception != null) {
                Log.d("fail", "fail")
            } else {


            }
        })
        */
        return activeUsers
    }


    fun checkCurrentGameId(): LiveData<String?> {
        gameRef.get().addOnCompleteListener { task ->
            if (task.isComplete) {
                for (doc in task.result) {
                    currentGameId.value = doc.id
                }
            }
        }
        return currentGameId
    }


}
