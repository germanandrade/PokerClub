package com.ramup.gandrade.pokerclub.UserProfile

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat.getColor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramup.gandrade.pokerclub.Global.ProfilePicDialog
import com.ramup.gandrade.pokerclub.Picasso.RoundTransformation
import com.ramup.gandrade.pokerclub.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_list_item.view.*
import org.koin.dsl.module.applicationContext

class UserAdapter(var map: MutableMap<String, User>, val context: Context) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    val keys = map.keys.toTypedArray()

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
        val user = map.get(keys[pos])!!
        holder.tvName.text = user.name
        holder.tvEndavans.text = user.endavans.toString()
        holder.tvDebt.text = user.debt.toString()
        holder.tvLifeSaver.text = user.lifeSavers.toString()
        val transformation = RoundTransformation()
        Picasso.get()
                .load(user.imageUrl)
                .placeholder(R.drawable.man)
                .transform(transformation)
                .fit()
                .into(holder.tvProfileImage)
        holder.tvProfileImage.setOnClickListener(View.OnClickListener {
            if (user.imageUrl != null)
                ProfilePicDialog(context, user.name, user.imageUrl!!).show()
        })
        if (user.admin) {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.name
        val tvEndavans = view.endavans
        val tvDebt = view.debt
        val tvLifeSaver = view.lifeSavers
        val tvProfileImage = view.profileImage
        val layout = view.layout
    }

}