package com.ramup.gandrade.pokerclub

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.ramup.gandrade.pokerclub.Login.LoginActivity
import com.ramup.gandrade.pokerclub.util.GlideApp
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel

class UserProfileActivity : FragmentActivity() {
    val userProfileViewModel by viewModel<UserProfileViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        userProfileViewModel.loggedIn.observe(this, Observer {
            loggedIn ->
            run { if (!loggedIn!!) startActivity<LoginActivity>() }
        })
        GlideApp.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/pokerclub-54146.appspot.com/o/defaultProfile.JPG?alt=media&token=084c5677-c710-4c87-9abd-683b459d452b")
                .placeholder(R.drawable.man)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePic)

        name.setText(userProfileViewModel.getName())

        userProfileViewModel.user.observe(this, Observer {
            //UpdateUI

            user ->
            run {
                debt.text = user?.debt.toString()
                endavans.text = user?.endavans.toString()
                name.text = user?.name
            }

        })
    }

    fun button(view: View) {
        startActivity<MainFunctionsActivity>()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signout -> userProfileViewModel.signOut()
        }
        return super.onOptionsItemSelected(item)
    }
}
