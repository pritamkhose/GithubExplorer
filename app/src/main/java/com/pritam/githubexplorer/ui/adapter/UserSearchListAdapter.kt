package com.pritam.githubexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.ListItemUserSerachBinding
import com.pritam.githubexplorer.retrofit.model.Item


class UserSearchListAdapter(private val userList: ArrayList<Item>) : RecyclerView.Adapter<UserSearchListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListItemUserSerachBinding>(LayoutInflater.from(parent.context), R.layout.list_item_user_serach,
            parent, false)
        return ViewHolder(binding)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = userList[position]
        holder.binding.executePendingBindings()
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    fun addItem(users: List<Item>) {
        this.userList.apply {
            addAll(users)
        }
    }

    fun clearItem() {
        this.userList.apply {
            clear()
        }
    }

    fun getItem(position: Int): Item {
        return userList[position]
    }

    //the class is holding the list view
    class ViewHolder(val binding: ListItemUserSerachBinding) : RecyclerView.ViewHolder(binding.root)
}
