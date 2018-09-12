package com.ramup.gandrade.pokerclub.game.views

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
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.game.GameViewModel
import com.ramup.gandrade.pokerclub.userprofile.User
import com.ramup.gandrade.pokerclub.util.LoadingDialog
import kotlinx.android.synthetic.main.fragment_game_start.*
import kotlinx.android.synthetic.main.fragment_game_start.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.koin.android.architecture.ext.viewModel

const val CAMERA_SCAN_REQUEST_CODE = 999

class GameStartFragment : Fragment(), View.OnClickListener {

    val gameViewModel by viewModel<GameViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_start, container, false)
        view.createGame.setOnClickListener(this)
        view.joinGame.setOnClickListener(this)
        view.continueGame.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeActiveOrPaused()
    }

    //Called each time fragment is visible
    fun observeActiveOrPaused() {
        checkActiveGame()
        checkPausedGame()
    }

    private fun checkActiveGame() {
        gameViewModel.checkActiveGames()
        gameViewModel.currentActiveGameId.observe(this, Observer { id ->
            observeCurrentGameId(id!!)
        })
    }

    private fun observeCurrentGameId(id: String) {
        gameViewModel.getUser()
        gameViewModel.user.observe(this, Observer { user ->
            observeUser(user)
        })
        //gameViewModel.currentActiveGameId.removeObservers(this)
    }

    private fun observeUser(user: User?) {
        if (user != null && user.active) {
            startGame()
        } else {
            joinGame.visibility = View.VISIBLE
            createGame.visibility = View.GONE
            continueGame.visibility = View.GONE
        }
        gameViewModel.user.removeObservers(this)


    }


    private fun checkPausedGame() {
        gameViewModel.checkPausedGame()
        gameViewModel.pausedGameId.observe(this, Observer { id ->
            turnOnContinueGameButton()
            //gameViewModel.pausedGameId.removeObservers(this)
        })
    }


    private fun turnOnContinueGameButton() {
        continueGame.visibility = View.VISIBLE
        createGame.visibility = View.GONE
        joinGame.visibility = View.GONE
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.createGame -> createGameM()
            R.id.joinGame -> joinGame()
            R.id.continueGame -> resumeGame()
        }
    }

    private fun createGameM() {

        createGame.isEnabled = false
        if (gameViewModel.currentActiveGameId == null || gameViewModel.currentActiveGameId.value != null
                || gameViewModel.pausedGameId.value != null) {
            Toast.makeText(context, "Can't create a game", Toast.LENGTH_SHORT).show()
        } else {
            gameViewModel.createGame()
            gameViewModel.currentActiveGameId.observe(this, Observer {
                gameViewModel.currentActiveGameId.removeObservers(this)
            })
        }
    }

    private fun startGame() {
        val loadingDialog = LoadingDialog(context)
        startActivity<GameActivity>()
        loadingDialog.cancel()
        activity!!.finish()
    }


    private fun resumeGame() {
        continueGame.isEnabled = false
        if (gameViewModel.pausedGameId.value == null) {
            Toast.makeText(context, "Can't resume game", Toast.LENGTH_SHORT).show()
        } else {
            removeObservers()
            gameViewModel.resumeGame()
            gameViewModel.successfulResume.observe(this, Observer { success ->
                if (success != null && success) {
                    startGame()
                } else {
                    Toast.makeText(context, "Can't resume game", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun removeObservers() {
        gameViewModel.currentActiveGameId.removeObservers(this)
        gameViewModel.pausedGameId.removeObservers(this)
        //gameViewModel.user.removeObservers(this)

    }

    private fun joinGame() {
        if (gameViewModel.currentActiveGameId == null) {
            Toast.makeText(context, "Can't join to game", Toast.LENGTH_SHORT).show()
        } else {
            val permission = ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Permission to camera denied", Toast.LENGTH_LONG)
                makeRequest()
            } else {
                joinGame.isEnabled = false
                startActivity<ScanActivity>()
                activity!!.finish()
            }
        }
    }


    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), CAMERA_SCAN_REQUEST_CODE)
        }
    }

    companion object {
        fun newInstance(): GameStartFragment {
            return GameStartFragment()
        }
    }
}
