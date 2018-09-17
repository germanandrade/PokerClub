package com.ramup.gandrade.pokerclub.leaderboard

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.model.User
import com.ramup.gandrade.pokerclub.userprofile.UserAdapter
import kotlinx.android.synthetic.main.fragment_global.*
import kotlinx.android.synthetic.main.fragment_global.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class LeaderboardFragment : Fragment() {
    val leaderboardViewModel by viewModel<LeaderboardViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        leaderboardViewModel.checkCurrentGameId()
        leaderboardViewModel.currentGameId.observe(this, Observer {
            leaderboardViewModel.fetchUsers()
            leaderboardViewModel.activeUsers.observe(this, Observer { list ->
                rv_user_list.adapter = UserAdapter(list!!, activity!!)
            })
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_global, container, false)
        view.rv_user_list.layoutManager = LinearLayoutManager(activity!!)

        view.rv_user_list.adapter = UserAdapter(mutableMapOf<String, User>(), activity!!)

        return view
    }

    companion object {
        fun newInstance(): LeaderboardFragment {
            return LeaderboardFragment()
        }
    }
}
