package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ramup.gandrade.pokerclub.User


class UserProfileRepository() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("balance").document(auth.currentUser?.uid.toString())
    val data = MutableLiveData<User>()
    fun fetch(): LiveData<User> {
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val newUser = User(document.data)
                    data.value = newUser
                } else {

                }
            } else {
            }
        }
        return data
    }

    fun buyEndavans(): Task<Void> {
        var currentDebt = data.value?.debt ?: 0
        var newUser = data.value?.copy(debt = currentDebt + 500)
                ?: User(auth.currentUser?.email ?: "none", 0, 500)
        data.value = newUser
        return docRef.set(newUser.toMap())
    }

    fun payDebt(): Task<Void> {
        var currentDebt = data.value?.debt ?: 0
        if (currentDebt == 0) {
            throw Exception("You have no debt")
        }
        var newUser = data.value?.copy(debt = 0)
                ?: User(auth.currentUser?.email ?: "none", 0, 0)
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


}
