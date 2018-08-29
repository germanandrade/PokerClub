package com.ramup.gandrade.pokerclub.Global

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.ramup.gandrade.pokerclub.UserProfile.User

class GlobalViewModel(val globalRepo: GlobalRepository) : ViewModel() {
    lateinit var activeUsers:LiveData<MutableMap<String, User>>
    lateinit var currentGameId: LiveData<String?>
    fun checkCurrentGameId() {
        currentGameId=globalRepo.checkCurrentGameId()
    }

    fun fetchUsers() {
        activeUsers=globalRepo.fetchUsers()
    }
}
