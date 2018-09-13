package com.ramup.gandrade.pokerclub

import com.google.firebase.FirebaseApp
import com.ramup.gandrade.pokerclub.game.Game
import com.ramup.gandrade.pokerclub.game.GameViewModel
import com.ramup.gandrade.pokerclub.userprofile.GameRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.declareMock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.mockito.Mockito.mock


class GameRepositoryUnitTest : AutoCloseKoinTest() {

    val gameRepository: GameRepository by inject()

    @Before
    fun before() {
        startKoin(listOf())
        declareMock<GameRepository>()
    }


    @Test
    fun tesKoinComponents() {
        val gameViewModel = GameViewModel(gameRepository)
        gameViewModel.createGame()

        //Mockito.verify(service).hello()
    }
}
