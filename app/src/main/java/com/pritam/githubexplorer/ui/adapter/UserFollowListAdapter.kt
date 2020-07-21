package com.pritam.githubexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.ListItemFollowBinding
import com.pritam.githubexplorer.retrofit.model.UserFollowResponse

class UserFollowListAdapter(private val userList: List<UserFollowResponse>) : RecyclerView.Adapter<UserFollowListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListItemFollowBinding>(LayoutInflater.from(parent.context), R.layout.list_item_follow,
            parent, false)
        return ViewHolder(binding)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.user = userList[position]
        holder.binding.executePendingBindings()
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is holding the list view
    class ViewHolder(val binding: ListItemFollowBinding) : RecyclerView.ViewHolder(binding.root)
}
