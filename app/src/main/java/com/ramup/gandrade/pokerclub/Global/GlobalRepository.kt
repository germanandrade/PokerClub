package com.ramup.gandrade.pokerclub.Global

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ramup.gandrade.pokerclub.UserProfile.User


class GlobalRepository() {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("balance")
    val data = MutableLiveData<List<User>>()

    fun fetch(): LiveData<List<User>> {
        val arr = ArrayList<User>(20)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    if (document.exists()) {
                        val newUser = User(document.data)
                        arr.add(newUser)
                    }
                }
                data.value = arr

            }
        }
        return data
    }




}
