package com.pritam.githubexplorer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.retrofit.model.UserReposResponse

class UserRepoListAdapter(val userList: ArrayList<UserReposResponse>) : RecyclerView.Adapter<UserRepoListAdapter.ViewHolder>() {

    lateinit var mContext: Context

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_repos, parent, false)
        mContext = parent.context
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

        fun bindItems(item: UserReposResponse) {
            val tvname = itemView.findViewById(R.id.tv_name) as TextView
            tvname.text = item.name
            val tvdesp = itemView.findViewById(R.id.tv_description) as TextView
            setTextView(tvdesp ,item.description)
            val tvlang = itemView.findViewById(R.id.tv_language) as TextView
            setTextView(tvlang ,item.language)
            val tvfork = itemView.findViewById(R.id.tv_forks_count) as TextView
            tvfork.text = item.forks_count.toString()
            val tvstr = itemView.findViewById(R.id.tv_stargazers_count) as TextView
            tvstr.text = item.stargazers_count.toString()
        }

        private fun setTextView(tvView: TextView, name: String) {
            if (name.isNullOrEmpty()) {
                tvView.visibility = View.GONE
            } else {
                tvView.visibility = View.VISIBLE
                tvView.text = name;
            }
        }
    }

}
