package com.ramup.gandrade.pokerclub.getstarted

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.fragment_get_started.view.*

class GetStartedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_get_started, container, false)
        view.textView.setText(arguments?.getString("text"))
        return view
    }


    companion object {
        fun newInstance(): GetStartedFragment {
            return GetStartedFragment()
        }
    }
}
