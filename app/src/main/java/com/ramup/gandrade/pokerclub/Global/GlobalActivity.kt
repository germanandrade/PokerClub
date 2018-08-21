package com.ramup.gandrade.pokerclub.Global

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import com.ramup.gandrade.pokerclub.R
import com.ramup.gandrade.pokerclub.UserAdapter
import kotlinx.android.synthetic.main.activity_global.*
import org.koin.android.architecture.ext.viewModel

class GlobalActivity : FragmentActivity() {
    val globalViewModel by viewModel<GlobalViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global)
        globalViewModel.users.observe(this, Observer { list ->
            rv_user_list.adapter = UserAdapter(list!!, this)
        })
        rv_user_list.layoutManager = LinearLayoutManager(this)
    }
}
