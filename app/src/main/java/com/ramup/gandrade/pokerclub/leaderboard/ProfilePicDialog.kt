package com.ramup.gandrade.pokerclub.leaderboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.ramup.gandrade.pokerclub.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_pic_dialog.*


class ProfilePicDialog(context: Context, val name: String, val image: String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(name)
        setContentView(R.layout.profile_pic_dialog)
        Picasso.get()
                .load(image)
                .resize(700, 700)
                .into(imagePic)
    }

}