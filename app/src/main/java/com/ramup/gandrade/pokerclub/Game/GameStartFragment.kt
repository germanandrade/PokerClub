package com.ramup.gandrade.pokerclub.Game

import android.Manifest
import android.app.Activity
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
import com.ramup.gandrade.pokerclub.ScanActivity
import kotlinx.android.synthetic.main.fragment_game_start.view.*
import org.jetbrains.anko.support.v4.startActivity

class GameStartFragment : Fragment(), View.OnClickListener {
    private val RECORD_REQUEST_CODE = 101
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.create -> startActivity<CreateGameActivity>()
            R.id.join -> setupPermissions()
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(activity!!.applicationContext,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission to record denied", Toast.LENGTH_LONG)
            makeRequest()
        }else{
            startActivity<ScanActivity>()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_start, container, false)
        view.create.setOnClickListener(this)
        view.join.setOnClickListener(this)
        return view
    }

    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(context as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    RECORD_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

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
