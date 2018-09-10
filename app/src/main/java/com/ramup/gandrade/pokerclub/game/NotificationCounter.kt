package com.ramup.gandrade.pokerclub.game

data class NotificationCounter(var currentId: Int = 0) {
    fun getId(): Int {
        return currentId++
    }
}
