package com.ramup.gandrade.pokerclub

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

class GlobalViewModel(val globalRepo: GlobalRepository) : ViewModel() {
    var users: LiveData<List<User>>

    init {
        users = globalRepo.fetch()
    }
}
