package com.ramup.gandrade.pokerclub.Game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gandrade.pokerclub.util.showMessage
import com.ramup.gandrade.pokerclub.R
import kotlinx.android.synthetic.main.fragment_game_start.view.*
import org.jetbrains.anko.support.v4.startActivity

class GameStartFragment : Fragment(), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.create -> startActivity<CreateGameActivity>()
            R.id.join -> showMessage(view, "lets join them!")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_start, container, false)
        view.create.setOnClickListener(this)
        view.join.setOnClickListener(this)
        return view
    }

    companion object {
        fun newInstance(): GameStartFragment {
            return GameStartFragment()
        }
    }
    fun readQr()
    {}
}
