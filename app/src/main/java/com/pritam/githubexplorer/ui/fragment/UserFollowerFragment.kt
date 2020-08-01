package com.pritam.githubexplorer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentFollowBinding
import com.pritam.githubexplorer.extensions.replaceFragment
import com.pritam.githubexplorer.retrofit.model.Status
import com.pritam.githubexplorer.retrofit.model.UserFollowResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiHelper
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserFollowListAdapter
import com.pritam.githubexplorer.ui.base.ViewModelFactory
import com.pritam.githubexplorer.ui.viewmodel.UserFollowerViewModel
import com.pritam.githubexplorer.utils.ConnectivityUtils
import java.util.*

class UserFollowerFragment : Fragment() {

    private lateinit var mBinding: FragmentFollowBinding
    private lateinit var viewModel: UserFollowerViewModel
    private var username = ""
    private lateinit var adapter: UserFollowListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("username", "pritamkhose")
        }
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        // Define the listener for binding
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_follow, container, false)

        setupUI()
        setupObservers()

        return mBinding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiClient.apiService))
        ).get(UserFollowerViewModel::class.java)
    }

    @SuppressLint("WrongConstant")
    private fun setupUI() {
        val context = activity as Context
        activity?.title = username.toUpperCase(Locale.ROOT) + " Followers"

        //Connect adapter with recyclerView
        adapter = UserFollowListAdapter(arrayListOf())
        mBinding.recyclerView.adapter = adapter

        //Add a LayoutManager
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        mBinding.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                mBinding.recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        openUserSerachFragment(adapter.getUsers(position).login)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )

        mBinding.swipeRefreshLayout.setColorSchemeResources(
            R.color.blue,
            R.color.green,
            R.color.orange,
            R.color.red
        )
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            setupObservers()
        }
    }

    private fun setupObservers() {
        if (activity?.baseContext?.let { ConnectivityUtils.isNetworkAvailable(it) }!!) {
            viewModel.getUserFollower(username).observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            mBinding.swipeRefreshLayout.isRefreshing = false
                            resource.data?.let { users -> retrieveList(users) }
                        }
                        Status.ERROR -> {
                            mBinding.swipeRefreshLayout.isRefreshing = false
                            Snackbar.make(
                                activity?.window?.decorView?.rootView!!,
                                R.string.error,
                                Snackbar.LENGTH_LONG
                            ).show()
                            Log.d("mTAG", it.message.toString())
                        }
                        Status.LOADING -> {
                            mBinding.swipeRefreshLayout.isRefreshing = true
                        }
                    }
                }
            })
        } else {
            // network is not present then show message
            Snackbar.make(
                activity?.window?.decorView?.rootView!!,
                R.string.network_error,
                Snackbar.LENGTH_LONG
            ).setAction("Retry") {
                setupObservers()
            }.show()
        }
    }

    private fun retrieveList(users: List<UserFollowResponse>) {
        adapter.apply {
            addUsers(users)
            notifyDataSetChanged()
        }
    }

    private fun openUserSerachFragment(username: String) {
        val fragment = UsersDetailsFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        replaceFragment(fragment, R.id.fragment_container)
    }

}