package com.ramup.gandrade.pokerclub.Game

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.LiveData
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
import android.widget.Button
import android.widget.Toast
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import kotlinx.android.synthetic.main.fragment_game_start.*
import kotlinx.android.synthetic.main.fragment_game_start.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.koin.android.architecture.ext.viewModel

class GameStartFragment : Fragment(), View.OnClickListener {

    val gameViewModel by viewModel<GameViewModel>()

    var activeGame: Boolean? = null
    var pauseGame: Boolean? = null

    private val CAMERA_REQUEST_CODE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_start, container, false)
        view.createGame.setOnClickListener(this)
        view.joinGame.setOnClickListener(this)
        view.continueGame.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameViewModel.checkActiveGames()
        gameViewModel.checkPausedGame()
        checkState("activeGame", gameViewModel.activeGameId, joinGame)
        checkState("pausedGame", gameViewModel.pausedGameId, continueGame)
    }

    private fun checkState(state: String, gameId: LiveData<String?>?, button: Button) {
        gameId?.observe(this, Observer { id ->
            if (id != null) {
                button.visibility = View.VISIBLE
            } else {
                when (state) {
                    "activeGame" -> activeGame = false
                    "pausedGame" -> pauseGame = false
                }
                checkNoActiveAndNoContinue()
            }
        })
    }

    private fun checkNoActiveAndNoContinue() {
        if (activeGame != null && pauseGame != null) {
            if (!activeGame!! && !pauseGame!!) {
                createGame.visibility = View.VISIBLE
            }
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.createGame -> startActivity<CreateGameActivity>()
            R.id.joinGame -> setupPermissions()
            R.id.continueGame -> setupPermissions()
        }
    }

    private fun resumeGame(id: String) {
        gameViewModel.resumeGame()
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
