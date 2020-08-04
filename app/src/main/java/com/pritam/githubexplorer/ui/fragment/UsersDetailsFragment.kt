package com.pritam.githubexplorer.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.Nullable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentUserDetailsBinding
import com.pritam.githubexplorer.extensions.replaceFragment
import com.pritam.githubexplorer.retrofit.model.UserDetailsResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.pritam.githubexplorer.utils.Constants
import com.pritam.githubexplorer.utils.Constants.Companion.GIST_URL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_user_details.*
import java.util.*
import java.util.regex.Pattern


open class UsersDetailsFragment : Fragment() {

    private var username = ""
    private lateinit var userObj: UserDetailsResponse
    private lateinit var mBinding: FragmentUserDetailsBinding
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            username = it.getString("username", "pritamkhose")
        }
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

        val context = activity as Context
        activity?.title = username.toUpperCase(Locale.ROOT)

        fetchdata(context)

        return mBinding.root
    }

    fun followers() {
        if (userObj.followers > 0) {
            openFragment(UserFollowerFragment())
        }
    }

    fun following() {
        if (userObj.following > 0) {
            openFragment(UserFollowingFragment())
        }
    }

    fun repos() {
        if (userObj.public_repos > 0) {
            openFragment(UserReposFragment())
        }
    }

    fun gist() {
        openCustomTabs(GIST_URL + username)
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


    private fun fetchdata(context: Context) {
        if (ConnectivityUtils.isNetworkAvailable(context)) {
            // network is present so will load updated data
            compositeDisposable.add(
                ApiClient.client.getUserDetails(username)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResults, this::handleError)
            )
        } else {
            // network is not present then show message
            Snackbar.make(
                activity?.window?.decorView?.rootView!!,
                R.string.network_error,
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry") {
                    fetchdata(context)
                }.show()
        }
    }

    private fun handleResults(aObj: UserDetailsResponse) {
        try {
            userObj = aObj
            mBinding.userdetails = aObj
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(
                activity?.window?.decorView?.rootView!!,
                R.string.error,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun handleError(t: Throwable) {
        Log.e(Constants.APP_TAG, t.toString())
        Snackbar.make(activity?.window?.decorView?.rootView!!, R.string.error, Snackbar.LENGTH_LONG)
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