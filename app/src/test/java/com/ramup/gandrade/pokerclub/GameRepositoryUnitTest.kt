package com.ramup.gandrade.pokerclub

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ramup.gandrade.pokerclub.game.notifications.NotificationApiService
import com.ramup.gandrade.pokerclub.userprofile.GameRepository
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.android.ext.koin.with
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.KoinTest
import org.mockito.Mockito.mock


class GameRepositoryUnitTest : KoinTest {

    @Test
    fun test() {
        startKoin(listOf(module {
            single {
                GameRepository(get(), get(), get())
            }
            single { NotificationApiService.create() }
            single { mock(FirebaseAuth::class.java) }

            single { mock(FirebaseFirestore::class.java) }
            /**
             *
            single {
            val op = FirebaseOptions.Builder()
            .setApplicationId("1:445828809799:android:c68ba0568000e9f8")
            .setDatabaseUrl("https://pokerclub-89f6e.firebaseio.com")
            .setApiKey("AIzaSyBer-ZZwJlceG-KAk5O_4nD26zeJE9JIaA")
            .setProjectId("pokerclub-89f6e")
            .setGcmSenderId("445828809799")
            .setStorageBucket("pokerclub-89f6e.appspot.com")
            .build()
            val a = FirebaseApp.initializeApp(mock(Context::class.java),op)
            a.applicationContext
            //mock(android.text.TextUtils::class.java)
            FirebaseAuth(a)

            }
             */

        })) with (mock(Context::class.java))
        val gameRepo = get<GameRepository>()
        assertNotNull(gameRepo)

        gameRepo.createGame()

        gameRepo.currentActiveGameId.observeForever { id ->
            assertNotNull(id)
            System.out.println(id)
        }
    }


}
