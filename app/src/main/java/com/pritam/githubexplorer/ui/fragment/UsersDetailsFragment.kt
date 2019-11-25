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
import com.pritam.githubexplorer.retrofit.rest.ApiInterface
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.pritam.githubexplorer.utils.Constants
import kotlinx.android.synthetic.main.fragment_user_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


open class UsersDetailsFragment : Fragment() {

    private val TAG = UsersDetailsFragment::class.java.simpleName
    private var username = "";
    private val apiService = ApiClient.client!!.create(ApiInterface::class.java)
    private lateinit var userObj: UserDetailsResponse
    private lateinit var mBinding: FragmentUserDetailsBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false);
        mBinding.listener = this

        val context = activity as Context
        activity?.setTitle(username.toUpperCase())

        fetchdata(context)

        return mBinding.getRoot();
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

    fun onclick_blog(txt: String) {
        openCustomTabs(txt)
    }

    fun onclick_email() {
        sendEmail()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.action_share -> {
                shareData()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun fetchdata(context: Context) {
        if (ConnectivityUtils.isNetworkAvailable(context)) {
            // network is present so will load updated data
            val call = apiService.getUserDetails(username)
            call.enqueue(object : Callback<UserDetailsResponse> {
                override fun onResponse(
                    call: Call<UserDetailsResponse>,
                    response: Response<UserDetailsResponse>
                ) {
                    val aObj: UserDetailsResponse? = response.body()
                    if (aObj !== null) {
                        try {
                            userObj = aObj
                            mBinding.userdetails = aObj
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Snackbar.make(
                            activity?.window?.decorView?.rootView!!,
                            R.string.error,
                            Snackbar.LENGTH_LONG
                        ).show();
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString())
                }
            })
        } else {
            // network is not present then show message
            Snackbar.make(
                activity?.window?.decorView?.rootView!!,
                R.string.network_error,
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry", View.OnClickListener {
                    fetchdata(context)
                }).show();
        }
    }


    private fun openCustomTabs(url: String) {
        if (url.length > 6 && url.contains("http")) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(getContext(), Uri.parse(url))
        }
    }

    private fun sendEmail() {
        var email = tv_email.text.toString();
        if (email.length > 6 && isEmailValid(email)) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, email)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, "Hi " + username + ",\n\nThanks & Regards,\n\n")
            startActivity(Intent.createChooser(intent, "Send Email!"))
        }
    }

    fun isEmailValid(email: String): Boolean {
        val EMAIL_REGEX = Pattern.compile(
            Constants.EMAIL_VERIFICATION,
            Pattern.CASE_INSENSITIVE
        )
        return EMAIL_REGEX.matcher(email).matches()
    }

    // Add data to the intent, the receiving app will decide
    private fun shareData() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Share " + username + " link!")
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.giturl) + username)
        startActivity(Intent.createChooser(share, "Share " + username + " link!"))
    }

    fun openFragment(fragment: Fragment) {
        val args = Bundle()
        args.putString("username", username)
        fragment.setArguments(args)
        replaceFragment(fragment, R.id.fragment_container)
    }

    fun stringtoDateFormat(dates: String): String {
        val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //2019-07-14T06:56:42Z
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        var dateStr = dates
        var date: Date
        try {
            date = SimpleDateFormat(DATE_FORMAT_PATTERN).parse(dates);
            dateStr = sdf.format(date).toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateStr
    }
}