package com.ramup.gandrade.pokerclub

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.ramup.gandrade.pokerclub.game.views.GameStartFragment
import com.ramup.gandrade.pokerclub.getstarted.GetStartedActivity
import com.ramup.gandrade.pokerclub.login.LoginActivity
import com.ramup.gandrade.pokerclub.userprofile.GameViewModel
import com.ramup.gandrade.pokerclub.userprofile.ProfileFragment
import com.ramup.gandrade.pokerclub.userprofile.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel


class Main2Activity : AppCompatActivity() {
    val gameViewModel by viewModel<GameViewModel>()
    val userProfileViewModel by viewModel<UserProfileViewModel>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_leaderboard -> {
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (!sharedPreferences.getBoolean("COMPLETE_GET_STARTED", false)) {
            startActivity<GetStartedActivity>()
            finish()
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        gameViewModel.loggedIn.observe(this, Observer { loggedIn ->
            logged(loggedIn!!).let { }
        })
        supportActionBar!!.hide()


        myViewPager.adapter = Main2ViewPagerAdapter(supportFragmentManager)
        myViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val name = makeFragmentName(myViewPager.getId(), position)
                var fragment: Fragment = supportFragmentManager.findFragmentByTag(name)
                if (fragment != null)
                {
                    if(fragment::class.simpleName.equals(GameStartFragment::class.simpleName)) {
                        supportActionBar!!.hide()
                    }
                    else if(fragment::class.simpleName.equals(ProfileFragment::class.simpleName)) {
                        supportActionBar!!.show()
                    }
                    else{
                        supportActionBar!!.hide()

                    }
                }
                navigation.menu.getItem(position).isChecked = true
            }
        })
    }

    fun logged(logged: Boolean) {
        startActivity<LoginActivity>()
        finish()
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
        val name = makeFragmentName(myViewPager.getId(), 2)
        var profileFragment: ProfileFragment = supportFragmentManager.findFragmentByTag(name) as ProfileFragment
        profileFragment.listenEdit()


    }

    private fun makeFragmentName(viewId: Int, id: Int): String {
        return "android:switcher:$viewId:$id"
    }
}
