package com.pritam.githubexplorer.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentUserDetailsBinding
import com.pritam.githubexplorer.retrofit.model.Status
import com.pritam.githubexplorer.retrofit.model.UserDetailsResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiHelper
import com.pritam.githubexplorer.ui.base.ViewModelFactory
import com.pritam.githubexplorer.ui.viewmodel.UserDetailsViewModel
import com.pritam.githubexplorer.utils.*


class UsersDetailsFragment : Fragment() {

    private var username = ""
    private lateinit var mBinding: FragmentUserDetailsBinding
    private lateinit var viewModel: UserDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            username = it.getString("username", "pritamkhose")
        }
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(ApiClient.apiService))
        ).get(UserDetailsViewModel::class.java)
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        // Define the listener for binding
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false)
        mBinding.listener = this

        setupUI()
        setupObservers()

        return mBinding.root
    }

    fun followers() {
        if (mBinding.userdetails?.followers!! > 0) {
            openFragment(UserFollowerFragment(), username)
        }
    }

    fun following() {
        if (mBinding.userdetails?.following!! > 0) {
            openFragment(UserFollowingFragment(), username)
        }
    }

    fun repos() {
        if (mBinding.userdetails?.public_repos!! > 0) {
            openFragment(UserReposFragment(), username)
        }
    }

    fun gist() {
        if (mBinding.userdetails?.public_gists!! > 0) {
            openFragment(UserGistFragment(), username)
        }
    }

    fun onClickBlog(txt: String) {
        openCustomTabs(txt)
    }

    fun onClickEmail() {
        sendEmail(mBinding.tvEmail.text.toString(), username)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_share -> {
                shareData(username, false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupUI() {
        activity?.title = username

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
        if (activity?.baseContext?.let { isNetworkAvailable() }!!) {
            viewModel.getUserDetails(username)
                .observe(viewLifecycleOwner, {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                mBinding.swipeRefreshLayout.isRefreshing = false
                                resource.data?.let { user -> retrieveData(user) }
                            }
                            Status.ERROR -> {
                                mBinding.swipeRefreshLayout.isRefreshing = false
                                snackBarError()
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

    private fun retrieveData(users: UserDetailsResponse) {
        try {
            mBinding.userdetails = users
        } catch (e: Exception) {
            e.printStackTrace()
            snackBarError()
        }
    }
}

