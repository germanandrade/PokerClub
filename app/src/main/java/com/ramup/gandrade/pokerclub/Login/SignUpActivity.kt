package com.ramup.gandrade.pokerclub.Login

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.example.gandrade.pokerclub.util.hideSoftKeyboard
import com.example.gandrade.pokerclub.util.isEmpty
import com.example.gandrade.pokerclub.util.passwordsMatch
import com.example.gandrade.pokerclub.util.showMessage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.ramup.gandrade.pokerclub.Main2Activity
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel

class SignUpActivity : FragmentActivity() {
    val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        emailSignUp.setText(intent.getStringExtra("email"))
        passwordSignUp.setText(intent.getStringExtra("password"))
    }

    fun signIn(view: View) {
        startActivity<LoginActivity>()
        finish()
    }

    fun submit(view: View) {
        hideSoftKeyboard(this)
        when {
            isEmpty(emailSignUp) || isEmpty(passwordSignUp) || isEmpty(displayName) -> showMessage(view, getString(R.string.fill_fields))
            !passwordsMatch(passwordSignUp, passwordRepeat) -> showMessage(view, "Passwords don't match")
            else -> {
                showMessage(view, "Loading...")
                viewModel.signUp(emailSignUp.text.toString(), passwordSignUp.text.toString())
                        .addOnCompleteListener(this, OnCompleteListener {
                            if (it.isSuccessful) {
                                viewModel.setDisplayName(displayName.text.toString())
                                startActivity<Main2Activity>()
                                finish()
                            } else {
                                showMessage(view, "Error: ${it.exception?.message}")
                            }
                        })
            }
        }

    }
}
