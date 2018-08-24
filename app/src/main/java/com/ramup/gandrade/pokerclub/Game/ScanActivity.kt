package com.ramup.gandrade.pokerclub.Game

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.content.Intent
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import org.jetbrains.anko.startActivity
import org.koin.android.architecture.ext.viewModel


class ScanActivity : FragmentActivity(), ZXingScannerView.ResultHandler {
    val gameViewModel by viewModel<GameViewModel>()

    override fun handleResult(rawResult: Result?) {
        val id = rawResult!!.text
        if (id.equals(gameViewModel.getCurrentGameId())) {
            gameViewModel.joinUser()
            gameViewModel.successfulJoin.observe(this, Observer { succes ->
                if (succes != null && succes) {
                    startActivity<GameActivity>()
                    finish()
                }
            })
        } else {
            showMessage(mScannerView, "$id differs gameViewModel.getCurrentGameId()")
        }
    }

    private lateinit var mScannerView: ZXingScannerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }

    public override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()           // Stop camera on pause
    }
}
