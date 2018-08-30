package com.ramup.gandrade.pokerclub.Login

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.example.gandrade.pokerclub.util.hideSoftKeyboard
import com.example.gandrade.pokerclub.util.isEmpty
import com.example.gandrade.pokerclub.util.showMessage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.ramup.gandrade.pokerclub.Main2Activity
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel


class LoginActivity : FragmentActivity() {
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (viewModel.isLogged()) {
            startActivity<Main2Activity>()
            finish()
        }
        setContentView(R.layout.activity_login)
    }

    fun submit(view: View) {
        hideSoftKeyboard(this)
        if (isEmpty(email) || isEmpty(password)) {
            showMessage(view, getString(R.string.fill_fields))
        } else {
            var emailString = email.text.toString()
            if(!emailString.contains("@"))
            {
                emailString+="@endava.com"
                email.setText(emailString)
            }
            showMessage(view, "Loading...")
            viewModel.sigIn(emailString, password.text.toString())
                    .addOnCompleteListener(this, OnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity<Main2Activity>()
                            finish()
                        } else {
                            showMessage(view, "Error: ${it.exception?.message}")
                        }
                    })
                    .addOnFailureListener {
                        showMessage(view, "Error: ${it.message}")
                    }
        }
    }

    fun signUp(view: View) {
        if (!isEmpty(email)) {
            startActivity<SignUpActivity>("email" to email.text.toString(), "password" to password.text.toString())
        } else {
            startActivity<SignUpActivity>()
        }
        finish()
    }
}
