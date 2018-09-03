package com.ramup.gandrade.pokerclub

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ramup.gandrade.pokerclub.Game.Views.GameStartFragment
import com.ramup.gandrade.pokerclub.Global.GlobalFragment
import com.ramup.gandrade.pokerclub.Login.LoginActivity
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import com.ramup.gandrade.pokerclub.UserProfile.ProfileFragment
import com.ramup.gandrade.pokerclub.UserProfile.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_view_pager.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel


class Main2Activity : FragmentActivity() {
    val gameViewModel by viewModel<GameViewModel>()
    val userProfileViewModel by viewModel<UserProfileViewModel>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_global -> {
                myViewPager.setCurrentItem(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_play -> {
                myViewPager.setCurrentItem(1)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                myViewPager.setCurrentItem(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_play
        gameViewModel.loggedIn.observe(this, Observer { loggedIn ->
            logged(loggedIn!!).let { }
        })

        myViewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        myViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                navigation.menu.getItem(position).isChecked = true
            }
        })
    }

    fun logged(logged: Boolean) {
        startActivity<LoginActivity>();finish()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signout -> gameViewModel.signOut()
            R.id.editProfile -> editProfile()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editProfile() {
        userProfileViewModel.editProfile()
        userProfileViewModel.editMode.observe(this, Observer { editMode ->
            navigation.selectedItemId = R.id.navigation_profile
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
        })
    }
}
