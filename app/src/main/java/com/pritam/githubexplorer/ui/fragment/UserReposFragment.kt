package com.pritam.githubexplorer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentUserReposBinding
import com.pritam.githubexplorer.retrofit.model.UserReposResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiInterface
import com.pritam.githubexplorer.ui.adapter.EndlessRecyclerOnScrollListener
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserRepoListAdapter
import com.pritam.githubexplorer.utils.ConnectivityUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


open class UserReposFragment : Fragment() {

    private val mtag = UserReposFragment::class.java.simpleName
    private lateinit var mBinding: FragmentUserReposBinding
    private var aList: ArrayList<UserReposResponse> = ArrayList()
    private var username = ""
    private var pageno = 1
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            username = it.getString("username", "")
        }
    }
    
    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Define the listener for binding
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_repos, container, false)

        val context = activity as Context
        activity?.title = username.toUpperCase(Locale.ROOT) + " Repositories"

        //Add a LayoutManager
        mBinding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        mBinding.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                mBinding.recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        openCustomTabs(aList[position].html_url)
                    }

                    override fun onLongClick(view: View?, position: Int) {
                    }
                })
        )

        mBinding.recyclerView.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                pageno += 1
                fetchdata(context)
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
            fetchdata(context)
        }
        fetchdata(context)

        return mBinding.root
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

    private fun fetchdata(context: Context) {
        if (ConnectivityUtils.isNetworkAvailable(context)) {
            if(!mBinding.swipeRefreshLayout.isRefreshing) {
                // Show swipe to refresh icon animation
                mBinding.swipeRefreshLayout.isRefreshing = true
                // network is present so will load updated data
                val apiService = ApiClient.client!!.create(ApiInterface::class.java)
                val call = apiService.getUserRepos(username, "updated", 25, pageno)
                call.enqueue(object : Callback<ArrayList<UserReposResponse>> {
                    override fun onResponse(
                        call: Call<ArrayList<UserReposResponse>>,
                        response: Response<ArrayList<UserReposResponse>>
                    ) {
                        // Hide swipe to refresh icon animation
                        mBinding.swipeRefreshLayout.isRefreshing = false
                        val alList: ArrayList<UserReposResponse>? = response.body()
                        if (alList !== null && alList.size > 0) {
                            //creating adapter and item adding to adapter of recyclerview
                            if(pageno == 1){
                                aList = alList
                                mBinding.recyclerView.adapter = UserRepoListAdapter(aList)
                            } else {
                                aList.addAll(alList)
                            }
                            if (aList.size > 0)
                                (mBinding.recyclerView.adapter as UserRepoListAdapter).notifyDataSetChanged()
                        } else {
                            Snackbar.make(
                                activity?.window?.decorView?.rootView!!,
                                R.string.nouser,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<UserReposResponse>>, t: Throwable) {
                        // Log error here since request failed
                        Log.e(mtag, t.toString())
                        mBinding.swipeRefreshLayout.isRefreshing = false
                    }
                })
            }
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

    private fun openCustomTabs(url: String) {
        if (url.length > 6 && url.contains("http")) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            context?.let { customTabsIntent.launchUrl(it, Uri.parse(url)) }
        }
    }


}
