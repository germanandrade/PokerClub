package com.ramup.gandrade.pokerclub

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramup.gandrade.pokerclub.UserProfile.UserProfileViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.koin.android.architecture.ext.viewModel

class ProfileFragment : Fragment() {
    val userProfileViewModel by viewModel<UserProfileViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/pokerclub-54146.appspot.com/o/defaultProfile.JPG?alt=media&token=084c5677-c710-4c87-9abd-683b459d452b")
                .placeholder(R.drawable.man)
                .into(profilePic)

        name.setText(userProfileViewModel.getName())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    companion object {
        fun newInstance():ProfileFragment{
            return ProfileFragment()
        }
    }
}
