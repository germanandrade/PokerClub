package com.ramup.gandrade.pokerclub.Global

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.ramup.gandrade.pokerclub.UserProfile.User

class GlobalViewModel(val globalRepo: GlobalRepository) : ViewModel() {

    var users: LiveData<List<User>>

    init {
        users = globalRepo.fetch()
    }
}
