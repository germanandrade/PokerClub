package com.ramup.gandrade.pokerclub.userprofile

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gandrade.pokerclub.util.bitmapToUriConverter
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.leaderboard.ProfilePicDialog
import com.ramup.gandrade.pokerclub.picasso.RoundTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.koin.android.architecture.ext.sharedViewModel
import java.lang.Exception


class ProfileFragment : Fragment(), View.OnClickListener, (DialogInterface, Int) -> Unit {

    private val CAMERA_REQUEST_CODE = 101
    private var editMode = false

    val GALLERY = 1
    val CAMERA = 2
    var bitmap: Bitmap? = null
    var uri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userProfileViewModel.checkCurrentGameId()
        userProfileViewModel.currentGameId.observe(this, Observer {
            userProfileViewModel.fetchUser()
            userProfileViewModel.user.observe(this, Observer { user ->
                updateUser(user!!)
            })
        })
        endEdit.setOnClickListener(this)

    }

    override fun invoke(p1: DialogInterface, pos: Int) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY && data != null) {
            uri = data.data
            bitmap = MediaStore.Images.Media.getBitmap(activity!!.getContentResolver(), data.data)

        } else if (requestCode == CAMERA && data != null) {
            bitmap = data.extras.get("data") as Bitmap
            uri = bitmapToUriConverter(bitmap!!, activity!!)
        }
        val newName = name.text.toString()
        userProfileViewModel.updateChanges(newName, bitmap)
        progressBar.visibility = View.VISIBLE

    }


    fun capturePicture() {
        val permission = ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission to camera denied", Toast.LENGTH_LONG)
            makeRequest()
        } else {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }

    }

    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Permission has been denied by user", Toast.LENGTH_LONG)
                } else {
                    val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA)
                }
            }
        }
    }

    fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.endEdit -> doneEdition()
        }
    }


    private fun doneEdition() {
        val newName = name.text.toString()
        userProfileViewModel.updateChanges(newName, bitmap)
        progressBar.visibility = View.VISIBLE
    }


    val userProfileViewModel by sharedViewModel<UserProfileViewModel>()
    private val TAG: String = ProfileFragment::class.java.simpleName


    fun listenEdit() {
        userProfileViewModel.editProfile()
        userProfileViewModel.editMode.observe(this, Observer { bool ->

            if (bool != null && bool) {
                endEdit.visibility = View.VISIBLE
                name.isClickable = true
                name.isCursorVisible = true
                name.isFocusable = true
                name.isFocusableInTouchMode = true

            } else if (bool != null && !bool) {
                endEdit.visibility = View.GONE
                name.isClickable = false
                name.isCursorVisible = false
                name.isFocusable = false
                name.isFocusableInTouchMode = false
                progressBar.visibility = View.GONE
                editMode = false

            }
        })
    }

    private fun updateUser(user: User) {
        profileLayout.visibility = View.VISIBLE
        profilePic.visibility = View.VISIBLE
        name.setText(user.name)
        debt.setText(user.debt.toString())
        endavans.setText(user.endavans.toString())
        lifesavers.setText(user.lifeSavers.toString())
        Picasso.get()
                .load(user.imageUrl)
                .placeholder(R.drawable.man)
                .transform(RoundTransformation())
                .fit()
                .into(profilePic, object : com.squareup.picasso.Callback {
                    override fun onError(e: Exception?) {
                        Log.e("ProfilePicDialog", e?.message)
                        progressBar.visibility = View.GONE
                    }

                    override fun onSuccess() {
                        //do smth when picture is loaded successfully
                        progressBar.visibility = View.GONE
                    }
                })
        setListeners(user.imageUrl)
    }


    private fun setListeners(imageUrl: String?) {
        profilePic.setOnClickListener(View.OnClickListener {
            ProfilePicDialog(activity!!, "", imageUrl ?: "nopath", this).show()
        })
        name.setOnClickListener {
            if (!editMode) {
                listenEdit()
                editMode = true
            }
        }
    }


    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
