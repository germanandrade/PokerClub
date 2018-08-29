package com.ramup.gandrade.pokerclub

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramup.gandrade.pokerclub.UserProfile.User
import kotlinx.android.synthetic.main.user_list_item.view.*

class UserAdapter(var map: MutableMap<String,User>, val context: Context) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    val keys =map.keys.toTypedArray()

    override fun getItemCount(): Int {
        return map.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.user_list_item,
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvName.text=map.get(keys[pos])!!.name
        holder.tvEndavans.text=map.get(keys[pos])!!.endavans.toString()
        holder.tvDebt.text=map.get(keys[pos])!!.debt.toString()
        holder.tvLifeSaver.text=map.get(keys[pos])!!.lifeSavers.toString()
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvName=view.name
        val tvEndavans=view.endavans
        val tvDebt=view.debt
        val tvLifeSaver=view.lifeSavers
    }

}
