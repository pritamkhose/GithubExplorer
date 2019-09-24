package com.pritam.githubexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.retrofit.model.UserFollowResponse
import com.squareup.picasso.Picasso

class UserFollowListAdapter(val userList: List<UserFollowResponse>) : RecyclerView.Adapter<UserFollowListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_user_serach, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }


    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: UserFollowResponse) {
            val textViewName = itemView.findViewById(R.id.textViewUsername) as TextView
            val im_avatar = itemView.findViewById(R.id.im_avatar) as ImageView
            textViewName.text = item.login
            if (item.avatar_url !== "") {
                Picasso.get()
                    .load(item.avatar_url)
                    .placeholder(R.mipmap.no_image_placeholder)
                    .into(im_avatar)
            }
        }
    }

}
