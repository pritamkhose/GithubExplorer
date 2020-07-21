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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentFollowBinding
import com.pritam.githubexplorer.extensions.replaceFragment
import com.pritam.githubexplorer.retrofit.model.UserFollowResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiInterface
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserFollowListAdapter
import com.pritam.githubexplorer.utils.ConnectivityUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class UserFollowingFragment : Fragment() {

    private val mtag = UserFollowerFragment::class.java.simpleName
    private lateinit var mBinding: FragmentFollowBinding
    private var username = ""
    private val apiService = ApiClient.client!!.create(ApiInterface::class.java)
    private var aListFollow: List<UserFollowResponse> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("username", "pritamkhose")
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        // Define the listener for binding
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_follow, container, false)

        val context = activity as Context
        activity?.title = username.toUpperCase(Locale.ROOT) +  " Following"
        
        //Add a LayoutManager
        mBinding.recyclerViewFollow.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        mBinding.recyclerViewFollow.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                mBinding.recyclerViewFollow,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        openUserSerachFragment(aListFollow[position].login)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )

        fetchFollowing(context)
        return mBinding.root
    }

    private fun fetchFollowing(context: Context) {
        if (ConnectivityUtils.isNetworkAvailable(context)) {
            val call = apiService.getUserFollowing(username)
            call.enqueue(object : Callback<List<UserFollowResponse>> {
                override fun onResponse(
                    call: Call<List<UserFollowResponse>>,
                    response: Response<List<UserFollowResponse>>
                ) {
                    val aList: List<UserFollowResponse>? = response.body()
                    if (aList != null && aList.isNotEmpty()) {
                        aListFollow = aList
                        mBinding.recyclerViewFollow.adapter = UserFollowListAdapter(aListFollow)
                    } else {
                        Snackbar.make(
                            activity?.window?.decorView?.rootView!!,
                            R.string.nouser,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<UserFollowResponse>>, t: Throwable) {
                    Log.e(mtag, t.toString())
                }
            })
        } else {
            // network is not present then show message
            Snackbar.make(
                activity?.window?.decorView?.rootView!!,
                R.string.network_error,
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry") {
                    fetchFollowing(context)
                }.show()
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