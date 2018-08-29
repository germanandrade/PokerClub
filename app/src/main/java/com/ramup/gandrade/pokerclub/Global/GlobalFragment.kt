package com.ramup.gandrade.pokerclub.Global

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserProfile.User
import com.ramup.gandrade.pokerclub.UserAdapter
import kotlinx.android.synthetic.main.activity_global.*
import kotlinx.android.synthetic.main.activity_global.view.*
import org.koin.android.architecture.ext.viewModel

class GlobalFragment : Fragment() {
    val globalViewModel by viewModel<GlobalViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        globalViewModel.users.observe(this, Observer { list ->
            Log.d("TAG!",list.toString())
            rv_user_list.adapter = UserAdapter(list!!, activity!!.applicationContext)
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view= inflater.inflate(R.layout.activity_global, container, false)
        view.rv_user_list.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        view.rv_user_list.adapter = UserAdapter(mutableMapOf<String,User>(), activity!!.applicationContext)

        return view
    }

    companion object {
        fun newInstance():GlobalFragment{
            return GlobalFragment()
        }
    }
}
