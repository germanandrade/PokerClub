package com.ramup.gandrade.pokerclub

import android.content.Context
import android.content.Intent
import com.ramup.gandrade.pokerclub.login.LoginActivity
import com.ramup.gandrade.pokerclub.login.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.Shadows.shadowOf


@RunWith(RobolectricTestRunner::class)
class WelcomeActivityTest : KoinTest {

    class ContextService(private val context: Context) {
        fun getString(stringId: Int): String = context.getString(stringId)
    }

    val contextService: ContextService by inject()

    @Before
    fun setUp() {
        val contextModule = applicationContext {
            bean { RuntimeEnvironment.application } bind Context::class
            factory { ContextService(get()) }
            viewModel { LoginViewModel() }
        }

        StandAloneContext.startKoin(listOf(contextModule))
        val context = application.applicationContext!!
        //FirebaseApp.initializeApp(context)

    }

    @After
    fun tearDown() {
        StandAloneContext.closeKoin()
    }

    @Test
    fun clickingLoginAndRealData_shouldStartMain2Activity() {
        val activity = Robolectric.setupActivity(LoginActivity::class.java!!)
        activity.email.setText("a")
        activity.password.setText("aaaaaa")
        activity.loginButton.performClick()

        val expectedIntent = Intent(activity, Main2Activity::class.java)
        val actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity()
        assertEquals(expectedIntent.component, actual.getComponent())
    }
}