package com.ramup.gandrade.pokerclub.leaderboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.R.drawable.man
import com.ramup.gandrade.pokerclub.userprofile.ProfileFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_pic_dialog.*
import java.lang.Exception


class ProfilePicDialog(context: Context, val name: String, val image: String, val profileFragment: ProfileFragment?) : Dialog(context), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.capturePictureButton -> profileFragment!!.capturePicture()
            R.id.chooseFromGalleryButton -> profileFragment!!.chooseFromGallery()
        }
        cancel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(name)
        setContentView(R.layout.profile_pic_dialog)
        Picasso.get()
                .load(image)
                .placeholder(man)
                .resize(700, 700)
                .into(
                        imagePic,
                        object : com.squareup.picasso.Callback {
                            override fun onError(e: Exception?) {
                                Log.e("ProfilePicDialog", e?.message)
                                loading.visibility = View.GONE
                            }

                            override fun onSuccess() {
                                //do smth when picture is loaded successfully
                                loading.visibility = View.GONE
                            }
                        }
                )
        if (profileFragment != null) {
            capturePictureButton.setOnClickListener(this)
            chooseFromGalleryButton.setOnClickListener(this)
        } else {
            capturePictureButton.visibility = View.GONE
            chooseFromGalleryButton.visibility = View.GONE
        }
    }


}