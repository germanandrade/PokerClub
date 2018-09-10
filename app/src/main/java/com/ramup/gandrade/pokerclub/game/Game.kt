package com.ramup.gandrade.pokerclub.game

import java.util.*

data class Game(val date: Long = Date().time, var state: GameState = GameState.ACTIVE) {
    fun toMap(): Map<String, Any> {
        return mapOf("Date" to date, "State" to state.toString())
    }
}

enum class GameState {
    ACTIVE, PAUSED, COMPLETE
}