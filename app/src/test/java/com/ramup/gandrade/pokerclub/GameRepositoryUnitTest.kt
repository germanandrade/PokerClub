package com.ramup.gandrade.pokerclub

import android.content.Context
import com.ramup.gandrade.pokerclub.game.GameViewModel
import com.ramup.gandrade.pokerclub.game.gameModule
import com.ramup.gandrade.pokerclub.userprofile.GameRepository
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.with
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
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
        startKoin(gameModule) with (mock(Context::class.java))
        val gameViewModel = GameViewModel(gameRepository)
        gameViewModel.createGame()

        //Mockito.verify(service).hello()
    }
}
