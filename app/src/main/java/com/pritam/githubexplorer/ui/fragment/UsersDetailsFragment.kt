package com.pritam.githubexplorer.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentUserDetailsBinding
import com.pritam.githubexplorer.extensions.replaceFragment
import com.pritam.githubexplorer.retrofit.model.Status
import com.pritam.githubexplorer.retrofit.model.UserDetailsResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiHelper
import com.pritam.githubexplorer.ui.base.ViewModelFactory
import com.pritam.githubexplorer.ui.viewmodel.UserDetailsViewModel
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.pritam.githubexplorer.utils.Constants
import com.pritam.githubexplorer.utils.Constants.Companion.GIST_URL
import kotlinx.android.synthetic.main.fragment_user_details.*
import java.util.*
import java.util.regex.Pattern


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
        setupViewModel()
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
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
            openFragment(UserFollowerFragment())
        }
    }

    fun following() {
        if (mBinding.userdetails?.following!! > 0) {
            openFragment(UserFollowingFragment())
        }
    }

    fun repos() {
        if (mBinding.userdetails?.public_repos!! > 0) {
            openFragment(UserReposFragment())
        }
    }

    fun gist() {
        if (mBinding.userdetails?.public_gists!! > 0) {
            openCustomTabs(GIST_URL + username)
        }
    }

    fun onClickBlog(txt: String) {
        openCustomTabs(txt)
    }

    fun onClickEmail() {
        sendEmail()
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

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiClient.apiService))
        ).get(UserDetailsViewModel::class.java)
    }

    private fun setupUI() {
        activity?.title = username.toUpperCase(Locale.ROOT)

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
            viewModel.getUserDetails(username)
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                mBinding.swipeRefreshLayout.isRefreshing = false
                                resource.data?.let { user -> retrieveData(user) }
                            }
                            Status.ERROR -> {
                                mBinding.swipeRefreshLayout.isRefreshing = false
                                Snackbar.make(
                                    activity?.window?.decorView?.rootView!!,
                                    R.string.error,
                                    Snackbar.LENGTH_LONG
                                ).show()
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
            Snackbar.make(
                activity?.window?.decorView?.rootView!!,
                R.string.error,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun openCustomTabs(url: String) {
        if (url.length > 6 && url.contains("http")) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            context?.let { customTabsIntent.launchUrl(it, Uri.parse(url)) }
        }
    }

    private fun sendEmail() {
        val email = tv_email.text.toString()
        if (email.length > 6 && isEmailValid(email)) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, email)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, "Hi $username,\n\nThanks & Regards,\n\n")
            startActivity(Intent.createChooser(intent, "Send Email!"))
        }
    }

    fun isEmailValid(email: String): Boolean {
        val emailRegx = Pattern.compile(
            Constants.EMAIL_VERIFICATION,
            Pattern.CASE_INSENSITIVE
        )
        return emailRegx.matcher(email).matches()
    }

    // Add data to the intent, the receiving app will decide
    private fun shareData() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Share $username link!")
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.giturl) + username)
        startActivity(Intent.createChooser(share, "Share $username link!"))
    }

    private fun openFragment(fragment: Fragment) {
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        replaceFragment(fragment, R.id.fragment_container)
    }
}

