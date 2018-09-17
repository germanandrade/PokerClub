package com.ramup.gandrade.pokerclub.leaderboard

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.ramup.gandrade.pokerclub.model.User

class LeaderboardViewModel(val leaderboardRepo: LeaderboardRepository) : ViewModel() {
    lateinit var activeUsers:LiveData<MutableMap<String, User>>
    lateinit var currentGameId: LiveData<String?>
    fun checkCurrentGameId() {
        currentGameId=leaderboardRepo.checkCurrentGameId()
    }

    fun fetchUsers() {
        activeUsers=leaderboardRepo.fetchUsers()
    }
}
