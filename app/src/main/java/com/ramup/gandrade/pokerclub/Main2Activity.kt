package com.ramup.gandrade.pokerclub

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.gandrade.pokerclub.util.disableABCShowHideAnimation
import com.ramup.gandrade.pokerclub.game.GameViewModel
import com.ramup.gandrade.pokerclub.game.views.CAMERA_SCAN_REQUEST_CODE
import com.ramup.gandrade.pokerclub.game.views.ScanActivity
import com.ramup.gandrade.pokerclub.getstarted.GetStartedActivity
import com.ramup.gandrade.pokerclub.login.LoginActivity
import com.ramup.gandrade.pokerclub.userprofile.CAMERA
import com.ramup.gandrade.pokerclub.userprofile.CAMERA_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.viewModel


class Main2Activity : AppCompatActivity() {
    val gameViewModel by viewModel<GameViewModel>()

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
        gameViewModel.takeCareOfLogOut()
        gameViewModel.loggedIn.observe(this, Observer { log() })
        val actionBar = supportActionBar!!
        actionBar.hide()


        myViewPager.adapter = Main2ViewPagerAdapter(supportFragmentManager)
        myViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {

                if (position == 2) {
                    disableABCShowHideAnimation(actionBar)
                    actionBar.show()
                } else {
                    disableABCShowHideAnimation(actionBar)
                    actionBar.hide()
                }
                navigation.menu.getItem(position).isChecked = true
            }
        })
    }

    fun log() {
        startActivity<LoginActivity>()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_SCAN_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.denied_by_user), Toast.LENGTH_LONG)
                } else {
                    startActivity<ScanActivity>()
                    finish()
                }
            }
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.denied_by_user), Toast.LENGTH_LONG)
                } else {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA)
                }
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signout -> gameViewModel.signOut()
        }
        return super.onOptionsItemSelected(item)
    }
}
