package com.ramup.gandrade.pokerclub.getstarted

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val COUNT = 4
    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment = GetStartedFragment()
        var myBundle = Bundle()
        val message = when (position) {
            0 -> "Poker club is not an app to play poker.\n It's actually an app to manage your casino chips."
            1 -> "Use it with your friends and collect real money to spend together"
            2 -> "Your casino chips expire every week to increase the fund \n You can buy casino chips with real money and easily manage the debt"
            3 -> "There is an administrator who must manage real money and casino chips and aprove transactions"
            else -> "a"
        }
        myBundle.putString("text", message)
        fragment.setArguments(myBundle)
        return fragment
    }

    override fun getCount(): Int {
        return COUNT
    }


}