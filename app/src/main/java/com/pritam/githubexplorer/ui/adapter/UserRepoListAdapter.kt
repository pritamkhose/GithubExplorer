package com.pritam.githubexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.ListItemReposBinding
import com.pritam.githubexplorer.retrofit.model.UserReposResponse

class UserRepoListAdapter(private val userList: ArrayList<UserReposResponse>) : RecyclerView.Adapter<UserRepoListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListItemReposBinding>(LayoutInflater.from(parent.context), R.layout.list_item_repos,
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
    class ViewHolder(val binding: ListItemReposBinding) : RecyclerView.ViewHolder(binding.root)

}
