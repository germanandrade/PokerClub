package com.ramup.gandrade.pokerclub.Global

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ramup.gandrade.pokerclub.UserProfile.User


class GlobalRepository() {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("balance")
    val data = MutableLiveData<MutableMap<String,User>>()

    fun fetch(): LiveData<MutableMap<String,User>> {
        val arr = mutableMapOf<String,User>()
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    if (document.exists()) {
                        val newUser = User(document.data)
                        arr.put(newUser.id,newUser)
                    }
                }
                data.value = arr

            }
        }
        return data
    }




}
