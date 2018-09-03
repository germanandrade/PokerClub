package com.ramup.gandrade.pokerclub.GetStarted

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.view.View
import com.ramup.gandrade.pokerclub.Main2Activity
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.activity_get_started.*
import org.jetbrains.anko.startActivity

class GetStartedActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        pager.adapter = ViewPagerAdapter(supportFragmentManager)
        tabDots.setupWithViewPager(pager, true)
    }

    fun finishGetStarted(view: View) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean("COMPLETE_GET_STARTED", true)
        editor.apply()
        startActivity<Main2Activity>()
        finish()
    }

}
