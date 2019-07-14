package com.pritam.githubexplorer.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.retrofit.model.UserDetailsResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiInterface
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class UsesDetailsFragment : Fragment() { //, View.OnClickListener


    private val TAG = UsesDetailsFragment::class.java.simpleName
    private lateinit var rootView: View
    private var username = "";

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

        if (ConnectivityUtils.isNetworkAvailable(context)) {
            // network is present so will load updated data
            fetchdata(username)
        } else {
            // network is not present then show message
            Snackbar.make(rootView!!, R.string.network_error, Snackbar.LENGTH_LONG).show();
        }

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

    private fun fetchdata(uname: String) {
        val apiService = ApiClient.client!!.create(ApiInterface::class.java)

        val call = apiService.getUserDetails(uname)
        call.enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                val aObj: UserDetailsResponse? = response.body()
                Log.d(TAG, "Response " + aObj.toString())
                if (aObj != null) {
                    tv_name.text = aObj.name;
                    tv_bio.text = aObj.bio;
                    tv_location.text = aObj.location;
                    tv_blog.text = aObj.blog;

                    if (null != aObj.email) {
                        tv_email.text = aObj.email.toString();
                    }

                    if (aObj.avatar_url != "") {
                        Picasso.get()
                            .load(aObj.avatar_url)
                            .placeholder(R.mipmap.no_image_placeholder)
                            .into(im_avatar)
                    }

                    tv_public_repos.text = aObj.public_repos.toString();
                    tv_public_gists.text = aObj.public_gists.toString();
                    tv_followers.text = aObj.followers.toString();
                    tv_following.text = aObj.following.toString();
                    tv_created_at.text = aObj.created_at;
                    tv_updated_at.text = aObj.updated_at;

                } else {
                    Snackbar.make(rootView!!, R.string.error, Snackbar.LENGTH_LONG).show();
                }
            }

            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
            }
        })
    }

    private fun openCustomTabs(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(getContext(), Uri.parse(url))
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL, tv_email.text.toString())
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, "Hi " + username + ",\n\n, Thanks & Regards,\n\n")
        startActivity(Intent.createChooser(intent, "Send Email"))
    }


    // Add data to the intent, the receiving app will decide
    private fun shareData() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Share " + username + " link!")
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.giturl) + username)
        startActivity(Intent.createChooser(share, "Share " + username + " link!"))
    }

}