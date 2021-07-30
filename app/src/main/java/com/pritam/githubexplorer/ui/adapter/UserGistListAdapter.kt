package com.pritam.githubexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.ListItemGistBinding
import com.pritam.githubexplorer.retrofit.model.UserGistResponse

class UserGistListAdapter(private val gistList: ArrayList<UserGistResponse>) : RecyclerView.Adapter<UserGistListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListItemGistBinding>(LayoutInflater.from(parent.context), R.layout.list_item_gist,
            parent, false)
        return ViewHolder(binding)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.gist = gistList[position]
        holder.binding.executePendingBindings()
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return gistList.size
    }

    fun addGist(users: List<UserGistResponse>) {
        this.gistList.apply {
            clear()
            addAll(users)
        }
    }

    fun getGist(position: Int): UserGistResponse {
        return gistList[position]
    }

    //the class is holding the list view
    class ViewHolder(val binding: ListItemGistBinding) : RecyclerView.ViewHolder(binding.root)
}
