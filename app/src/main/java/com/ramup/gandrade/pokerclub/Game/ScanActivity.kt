package com.ramup.gandrade.pokerclub.Game

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.content.Intent
import com.ramup.gandrade.pokerclub.UserProfile.GameViewModel
import org.koin.android.architecture.ext.viewModel


class ScanActivity : FragmentActivity(), ZXingScannerView.ResultHandler {
    val userProfileViewModel by viewModel<GameViewModel>()

    override fun handleResult(rawResult: Result?) {
        val id = rawResult!!.text
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("id", rawResult!!.text)

        startActivity(intent)


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
        mScannerView!!.stopCamera()           // Stop camera on pause
    }
}
