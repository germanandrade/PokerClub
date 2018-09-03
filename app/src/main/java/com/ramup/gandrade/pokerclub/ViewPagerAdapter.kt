package com.ramup.gandrade.pokerclub

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ramup.gandrade.pokerclub.Game.Views.GameStartFragment
import com.ramup.gandrade.pokerclub.Global.GlobalFragment
import com.ramup.gandrade.pokerclub.UserProfile.ProfileFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val COUNT = 3
    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = GlobalFragment()
            1 -> fragment = GameStartFragment()
            2 -> fragment = ProfileFragment()
        }
        return fragment
    }

    override fun getCount(): Int {
        return COUNT
    }


}