package com.pritam.githubexplorer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentFollowBinding
import com.pritam.githubexplorer.retrofit.model.Status
import com.pritam.githubexplorer.retrofit.model.UserReposResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiHelper
import com.pritam.githubexplorer.ui.adapter.EndlessRecyclerOnScrollListener
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserRepoListAdapter
import com.pritam.githubexplorer.ui.base.ViewModelFactory
import com.pritam.githubexplorer.ui.viewmodel.UserReposViewModel
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.pritam.githubexplorer.utils.LogUtils
import java.util.*


open class UserReposFragment : Fragment() {

    private lateinit var mBinding: FragmentFollowBinding
    private lateinit var viewModel: UserReposViewModel
    private var username = ""
    private var pageno = 1
    private lateinit var adapter: UserRepoListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            username = it.getString("username", "")
        }
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        ).get(UserReposViewModel::class.java)
    }

    @SuppressLint("WrongConstant")
    private fun setupUI() {
        val context = activity as Context
        activity?.title = username.uppercase(Locale.ROOT) + " Repositories"

        //Connect adapter with recyclerView
        adapter = UserRepoListAdapter(arrayListOf())
        mBinding.recyclerView.adapter = adapter

        //Add a LayoutManager
        mBinding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        mBinding.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                mBinding.recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        openCustomTabs(adapter.getRepos(position).html_url)
                    }

                    override fun onLongClick(view: View?, position: Int) {
                    }
                })
        )

        mBinding.recyclerView.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                pageno += 1
                setupObservers()
            }
        })

        mBinding.swipeRefreshLayout.setColorSchemeResources(
            R.color.blue,
            R.color.green,
            R.color.orange,
            R.color.red
        )
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            pageno = 1
            setupObservers()
        }
    }

    private fun setupObservers() {
        if (activity?.baseContext?.let { ConnectivityUtils.isNetworkAvailable(it) }!!) {
            viewModel.getUserRepos(username, "updated", 25, pageno).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                it?.let {  resource ->
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
                            LogUtils.debug("mTAG", it.message.toString())
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

    private fun retrieveList(repos: List<UserReposResponse>) {
        adapter.apply {
            if (pageno == 1) {
                clearRepos()
            }
            addRepos(repos)
            notifyDataSetChanged()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_share -> {
                shareData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Add data to the intent, the receiving app will decide
    private fun shareData() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Share $username link!")
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.giturl) + username)
        startActivity(Intent.createChooser(share, "Share $username link!"))
    }

    private fun openCustomTabs(url: String) {
        if (url.length > 6 && url.contains("http")) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            context?.let { customTabsIntent.launchUrl(it, Uri.parse(url)) }
        }
    }

}
