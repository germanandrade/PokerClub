package com.ramup.gandrade.pokerclub.userprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap

class UserProfileViewModel(val userRepo: UserProfileRepository) : ViewModel() {


    lateinit var user: LiveData<User>
    lateinit var currentGameId: LiveData<String?>
    var editMode = MutableLiveData<Boolean>()
    fun checkCurrentGameId() {
        currentGameId = userRepo.checkCurrentGameId()
    }

    fun fetchUser() {
        user = userRepo.fetchUser()
    }

    fun editProfile() {
        editMode = userRepo.editProfile()
    }

    fun updateChanges(newName: String, bitmap: Bitmap?) {
        editMode = userRepo.updateChanges(newName,bitmap)
    }


}

