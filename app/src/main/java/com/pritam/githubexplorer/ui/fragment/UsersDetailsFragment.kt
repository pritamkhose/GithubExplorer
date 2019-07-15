package com.pritam.githubexplorer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.retrofit.model.UserDetailsResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiInterface
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.pritam.githubexplorer.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


open class UsersDetailsFragment : Fragment() {

    private val TAG = UsersDetailsFragment::class.java.simpleName
    private lateinit var rootView: View
    private var username = "";
    private val apiService = ApiClient.client!!.create(ApiInterface::class.java)
    private lateinit var userObj: UserDetailsResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        arguments?.let {
            username = it.getString("username", "pritamkhose")
        }
    }

    @SuppressLint("WrongConstant")
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_details, container, false);

        val context = activity as Context
        getActivity()?.setTitle(username);

        val tvemail: (TextView) = rootView.findViewById(R.id.tv_email)
        tvemail.setOnClickListener {
            sendEmail()
        }

        val tvblog: (TextView) = rootView.findViewById(R.id.tv_blog)
        tvblog.setOnClickListener {
            openCustomTabs(tv_blog.text.toString())
        }

        val cardViewFollower: (CardView) = rootView.findViewById(R.id.cardViewFollower)
        cardViewFollower.setOnClickListener {
            if(userObj != null && userObj.followers != null && userObj.followers > 0 ){
                openFragment(UserFollowerFragment())
            }
        }

        val cardViewFollowing: (CardView) = rootView.findViewById(R.id.cardViewFollowing)
        cardViewFollowing.setOnClickListener {
            if(userObj != null && userObj.following != null && userObj.following > 0 ){
                openFragment(UserFollowingFragment())
            }
        }

        val cardViewRepo: (CardView) = rootView.findViewById(R.id.cardViewRepo)
        cardViewRepo.setOnClickListener {
            if(userObj != null && userObj.public_repos != null && userObj.public_repos > 0 ){
                openFragment(UserReposFragment())
            }
        }

        fetchdata(context)

        return rootView
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
                override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                    val aObj: UserDetailsResponse? = response.body()
                    if (aObj != null) {
                        try{
                            userObj = aObj
                            val tv_name = rootView.findViewById(R.id.tv_name) as TextView
                            val tv_bio = rootView.findViewById(R.id.tv_bio) as TextView
                            val tv_blog = rootView.findViewById(R.id.tv_blog) as TextView
                            val tv_location = rootView.findViewById(R.id.tv_location) as TextView
                            val tv_email = rootView.findViewById(R.id.tv_email) as TextView
                            val tv_public_repos = rootView.findViewById(R.id.tv_public_repos) as TextView
                            val tv_public_gists = rootView.findViewById(R.id.tv_public_gists) as TextView
                            val tv_followers = rootView.findViewById(R.id.tv_followers) as TextView
                            val tv_following = rootView.findViewById(R.id.tv_following) as TextView
                            val tv_created_at = rootView.findViewById(R.id.tv_created_at) as TextView
                            val tv_updated_at = rootView.findViewById(R.id.tv_updated_at) as TextView
                            val im_avatar = rootView.findViewById(R.id.im_avatar) as ImageView

                            setTextView(tv_name, aObj.name)
                            setTextView(tv_bio, aObj.bio)
                            setTextView(tv_location, aObj.location)
                            setTextView(tv_blog, aObj.blog)
                            if (null != aObj.email) {
                                setTextView(tv_email, aObj.email.toString())
                            } else {
                                setTextView(tv_email, "")
                            }

                            if (aObj.avatar_url != "") {
                                Picasso.get()
                                    .load(aObj.avatar_url)
                                    .placeholder(R.mipmap.no_image_placeholder)
                                    .into(im_avatar)
                            }

                            setTextView(tv_public_repos, aObj.public_repos.toString())
                            setTextView(tv_public_gists, aObj.public_gists.toString())
                            setTextView(tv_followers, aObj.followers.toString())
                            setTextView(tv_following, aObj.following.toString())
                            setTextView(tv_created_at, getString(R.string.joinedAt) + stringtoDateFormat(aObj.created_at))
                            setTextView(tv_updated_at, getString(R.string.updatedAt) + stringtoDateFormat(aObj.updated_at))
                        } catch (e: Exception){
                            e.printStackTrace()
                        }

                    } else {
                        Snackbar.make(rootView!!, R.string.error, Snackbar.LENGTH_LONG).show();
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString())
                }
            })
        } else {
            // network is not present then show message
            Snackbar.make(rootView, R.string.network_error, Snackbar.LENGTH_LONG)
                .setAction("Retry", View.OnClickListener {
                    fetchdata(context)
                }).show();
        }
    }

    private fun setTextView(tvView: TextView, name: String) {
        if (name.isNullOrEmpty()) {
            tvView.visibility = View.GONE
        } else {
            tvView.visibility = View.VISIBLE
            tvView.text = name;
        }
    }

    private fun openCustomTabs(url: String) {
        if (url != null && url.length > 6 && url.contains("http")) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(getContext(), Uri.parse(url))
        }
    }

    private fun sendEmail() {
        if (username != null && username.length > 6 && isEmailValid(username)) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, tv_email.text.toString())
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

    private fun openFragment(fragment: Fragment) {
            val args = Bundle()
            args.putString("username", username)
            fragment.setArguments(args)
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
    }

    fun stringtoDateFormat(dates: String): String {
        val DATE_FORMAT_PATTERN  = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //2019-07-14T06:56:42Z
        val sdf = SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH)
        var dateStr= dates
        var date: Date? = null
        try {
            //date =
            date = SimpleDateFormat(DATE_FORMAT_PATTERN).parse(dates);
            dateStr =  sdf.format(date).toString()
            // println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateStr
    }
}