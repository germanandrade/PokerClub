package com.ramup.gandrade.pokerclub.Game

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserProfile.UserProfileViewModel
import kotlinx.android.synthetic.main.fragment_game_start.*
import kotlinx.android.synthetic.main.fragment_game_start.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.koin.android.architecture.ext.viewModel

class GameStartFragment : Fragment(), View.OnClickListener {
    val userProfileViewModel by viewModel<UserProfileViewModel>()
    var active: Boolean? = null
    var pause: Boolean? = null
    private val CAMERA_REQUEST_CODE = 101
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.create -> startActivity<CreateGameActivity>()
            R.id.join -> setupPermissions()
            R.id.continueGame -> setupPermissions()
        }
    }

    private fun resumeGame(id: String) {
        userProfileViewModel.resumeGame()
        startActivity<GameActivity>("id" to id)
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(activity!!.applicationContext,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission to camera denied", Toast.LENGTH_LONG)
            makeRequest()
        } else {
            startActivity<ScanActivity>()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userProfileViewModel.checkActiveGames()
        userProfileViewModel.checkPausedGame()

        checkIfActive()
        checkIfPaused()

    }

    private fun checkIfActive() {
        userProfileViewModel.activeGameId?.observe(this, Observer { id ->
            if (id != null) {

                join.visibility = View.VISIBLE
            } else {
                active = false
                showMessage(join, "fue nulo el activeGameId")
                checkNoActiveAndNoContinue()
            }
        })
    }

    private fun checkNoActiveAndNoContinue() {
        if (active != null && pause != null) {
            if (!active!! && !pause!!) {
                create.visibility = View.VISIBLE

            }
        }
    }

    private fun checkIfPaused() {
        userProfileViewModel.pausedGameId.observe(this, Observer { id ->
            if (id != null) {

                continueGame.visibility = View.VISIBLE
            } else {
                pause = false
                checkNoActiveAndNoContinue()
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_start, container, false)
        view.create.setOnClickListener(this)
        view.join.setOnClickListener(this)
        view.continueGame.setOnClickListener(this)
        return view
    }

    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(context as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Permission has been denied by user", Toast.LENGTH_LONG)

                } else {
                    startActivity<ScanActivity>()
                }
            }
        }
    }

    companion object {
        fun newInstance(): GameStartFragment {
            return GameStartFragment()
        }
    }

}
