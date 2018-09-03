package com.ramup.gandrade.pokerclub.GetStarted

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import com.ramup.gandrade.pokerclub.UserProfile.User
import kotlinx.android.synthetic.main.fragment_game_start.*
import kotlinx.android.synthetic.main.fragment_game_start.view.*
import kotlinx.android.synthetic.main.fragment_get_started.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.koin.android.architecture.ext.viewModel

class GetStartedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_get_started, container, false)
        view.textView.setText(arguments?.getString("text"))
        return view
    }


    companion object {
        fun newInstance(): GetStartedFragment {
            return GetStartedFragment()
        }
    }
}
