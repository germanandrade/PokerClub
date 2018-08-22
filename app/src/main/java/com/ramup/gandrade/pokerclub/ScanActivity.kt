package com.ramup.gandrade.pokerclub

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.zxing.Result
import com.ramup.gandrade.pokerclub.Login.SignUpActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.EditText
import android.content.Intent




class ScanActivity : FragmentActivity(), ZXingScannerView.ResultHandler {
    override fun handleResult(rawResult: Result?) {
        Toast.makeText(this, rawResult!!.text, Toast.LENGTH_LONG)
        val intent = Intent(this, SignUpActivity::class.java)
        intent.putExtra("email", rawResult!!.text)

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
