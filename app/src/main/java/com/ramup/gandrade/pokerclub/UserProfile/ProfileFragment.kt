package com.ramup.gandrade.pokerclub.UserProfile

import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gandrade.pokerclub.util.bitmapToUriConverter
import com.ramup.gandrade.pokerclub.Global.ProfilePicDialog
import com.ramup.gandrade.pokerclub.Picasso.RoundTransformation
import com.ramup.gandrade.pokerclub.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.koin.android.architecture.ext.sharedViewModel


class ProfileFragment : Fragment(), View.OnClickListener, (DialogInterface, Int) -> Unit {

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
        when (pos) {
            0 -> chooseFromGallery()
            1 -> capturePicture()
        }
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
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.man)
                .into(profilePic)

    }


    private fun capturePicture() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.endEdit -> doneEdition()
            R.id.profilePic -> changeProfilePic()
        }
    }


    private fun changeProfilePic() {
        showPictureDialog()
    }

    private fun showPictureDialog() {

        val builder: android.app.AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = android.app.AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = android.app.AlertDialog.Builder(activity)
        }
        var arr = Array<CharSequence>(2, { "Gallery" })
        arr[1] = "Camera"
        builder.setItems(arr, DialogInterface.OnClickListener(this))
        builder.show()
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
                profilePic.setOnClickListener(this)

            } else if (bool != null && !bool) {
                endEdit.visibility = View.GONE
                name.isClickable = false
                name.isCursorVisible = false
                name.isFocusable = false
                name.isFocusableInTouchMode = false
                progressBar.visibility = View.GONE

            }
        })
    }

    private fun updateUser(user: User) {
        profileLayout.visibility=View.VISIBLE
        profilePic.visibility=View.VISIBLE
        name.setText(user.name)
        debt.setText(user.debt.toString())
        endavans.setText(user.endavans.toString())
        lifesavers.setText(user.lifeSavers.toString())
        Picasso.get()
                .load(user.imageUrl)
                .placeholder(R.drawable.man)
                .transform(RoundTransformation())
                .fit()
                .into(profilePic)
        setImageListener(user.imageUrl)
    }

    private fun setImageListener(imageUrl:String?) {
        profilePic.setOnClickListener(View.OnClickListener {
            if (imageUrl != null) {
                ProfilePicDialog(activity!!, "", imageUrl!!).show()
            }
        })
    }




    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
