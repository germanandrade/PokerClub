package com.ramup.gandrade.pokerclub.userprofile

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.gandrade.pokerclub.util.ifNotNull
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.leaderboard.ProfilePicDialog
import com.ramup.gandrade.pokerclub.picasso.RoundTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.koin.android.architecture.ext.sharedViewModel
import java.lang.Exception

internal const val CAMERA_REQUEST_CODE = 101
private const val GALLERY = 1
internal const val CAMERA = 2

class ProfileFragment : Fragment(), View.OnClickListener, (DialogInterface, Int) -> Unit {

    private var editMode = false
    //private lateinit var bitmap: Bitmap
    //private lateinit var uri: Uri
    private val TAG: String = ProfileFragment::class.java.simpleName
    private val userProfileViewModel by sharedViewModel<UserProfileViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userProfileViewModel.checkCurrentGameId()
        userProfileViewModel.currentGameId.observe(this, Observer {
            userProfileViewModel.fetchUser()
            userProfileViewModel.user.observe(this, Observer { user ->
                updateUser(requireNotNull(user) { "$TAG user was null while fetchUser()" })
            })
        })
        endEdit.setOnClickListener(this)

    }

    override fun invoke(p1: DialogInterface, pos: Int) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ifNotNull(data, activity) { mData, mActivity ->
            run {
                if (requestCode == GALLERY) {
                    update(MediaStore.Images.Media.getBitmap(activity?.getContentResolver(), mData.data)
                    )
                } else if (requestCode == CAMERA) {
                    update(mData.extras.get("data") as Bitmap)
                }
            }
        }
    }

    fun update(bitmap: Bitmap) {
        userProfileViewModel.uploadImage(bitmap)
        progressBar.visibility = View.VISIBLE
    }


    fun capturePicture() {
        activity?.let {
            val permission = ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.CAMERA)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Permission to camera denied", Toast.LENGTH_LONG)
                makeRequest()
            } else {
                val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }
        }

    }

    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            Toast.makeText(context, "called", Toast.LENGTH_SHORT)
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
        userProfileViewModel.updateName(newName)
        progressBar.visibility = View.VISIBLE
    }


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
                        progressBar?.visibility = View.GONE
                    }

                    override fun onSuccess() {
                        //do smth when picture is loaded successfully
                        progressBar?.visibility = View.GONE
                    }
                })
        setListeners(user.imageUrl)
    }


    private fun setListeners(imageUrl: String?) {
        profilePic.setOnClickListener(View.OnClickListener {
            activity?.let {
                ProfilePicDialog(it, "", imageUrl ?: "nopath", this).show()
            }
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
