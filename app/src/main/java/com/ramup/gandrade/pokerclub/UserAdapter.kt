package com.ramup.gandrade.pokerclub

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.user_list_item.view.*

class UserAdapter(var arrayList: List<User>, val context: Context) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.user_list_item,
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvName.text=arrayList.get(pos).name
        holder.tvEndavans.text=arrayList.get(pos).endavans.toString()
        holder.tvDebt.text=arrayList.get(pos).debt.toString()
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvName=view.name
        val tvEndavans=view.endavans
        val tvDebt=view.debt
    }

}
