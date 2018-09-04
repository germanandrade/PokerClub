package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class UserProfileRepository() {
    private val db = FirebaseFirestore.getInstance()
    private val gameRef = db.collection("games")
    private val auth = FirebaseAuth.getInstance()
    var storage = FirebaseStorage.getInstance()


    private val currentGameId = MutableLiveData<String>()
    private val user = MutableLiveData<User>()
    val editMode = MutableLiveData<Boolean>()


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

    fun fetchUser(): LiveData<User> {
        val userDoc = gameRef.document(currentGameId.value!!).collection("users").document(auth.currentUser!!.uid)
        userDoc.addSnapshotListener(EventListener { query, exception ->
            if (exception != null) {
                Log.d("fail", "fail")
            } else {
                if (query.exists()) {
                    user.value = User(query.data)
                }
            }
        })

        return user
    }

    fun editProfile(): MutableLiveData<Boolean> {
        editMode.value = editMode.value == null || !editMode.value!!
        return editMode
    }

    fun updateChanges(newDisplayName: String, bitmap: Bitmap?): MutableLiveData<Boolean> {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(newDisplayName).build()
        user?.updateProfile(profileUpdates)
        if (bitmap != null) {
            uploadImage(bitmap, newDisplayName)
        } else {
            val userDoc = gameRef.document(currentGameId.value!!).collection("users").document(auth.currentUser!!.uid)
            userDoc.update("Name", newDisplayName)
                    .addOnSuccessListener {
                        editProfile() }
                    .addOnFailureListener{
                        Log.d("a",it.message)
                    }
        }
        return editMode

    }

    fun uploadImage(bitmap: Bitmap, newDisplayName: String) {
        val storageRef = storage.reference
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataobj = Date()
        val imagePath = auth.currentUser!!.uid + "." + df.format(dataobj) + ".jpg"
        val ImageRef = storageRef.child("imagePost/" + imagePath)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        ImageRef.putBytes(data)
                .addOnSuccessListener { taskSnapshot ->
                    val url = taskSnapshot.downloadUrl!!.toString()
                    val userDoc = gameRef.document(currentGameId.value!!).collection("users").document(auth.currentUser!!.uid)
                    userDoc.update("ImgUrl", url, "Name", newDisplayName)
                    editProfile()
                }
    }
}


