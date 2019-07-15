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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.retrofit.model.UserFollowResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiInterface
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserFollowListAdapter
import com.pritam.githubexplorer.utils.ConnectivityUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserFollowingFragment : Fragment() {

    private val TAG = UserFollowingFragment::class.java.simpleName
    private lateinit var rootView: View
    private var username = "";
    private val apiService = ApiClient.client!!.create(ApiInterface::class.java)
    private lateinit var recyclerViewFollowing: RecyclerView
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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_follow, container, false);

        val context = activity as Context
        getActivity()?.setTitle(username.toUpperCase() + " Following");

        //Bind the recyclerview
        recyclerViewFollowing = rootView.findViewById(R.id.recyclerViewFollow) as RecyclerView

        //Add a LayoutManager
        recyclerViewFollowing.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        recyclerViewFollowing.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                recyclerViewFollowing,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        openUserSerachFragment(aListFollow[position].login)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )

        fetchFollowing(context)
        return rootView
    }

    private fun fetchFollowing(context: Context) {
        if (ConnectivityUtils.isNetworkAvailable(context)) {
            val call = apiService.getUserFollowing(username)
            call.enqueue(object : Callback<List<UserFollowResponse>> {
                override fun onResponse(
                    call: Call<List<UserFollowResponse>>,
                    response: Response<List<UserFollowResponse>>
                ) {
                    var aList: List<UserFollowResponse>? = response.body()
                    if (aList != null && aList.size > 0) {
                        aListFollow = aList
                        recyclerViewFollowing.adapter = UserFollowListAdapter(aListFollow);
                    } else {
                        Snackbar.make(rootView, R.string.nouser, Snackbar.LENGTH_LONG).show();
                    }
                }

                override fun onFailure(call: Call<List<UserFollowResponse>>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        } else {
            // network is not present then show message
            Snackbar.make(rootView, R.string.network_error, Snackbar.LENGTH_LONG)
                .setAction("Retry", View.OnClickListener {
                    fetchFollowing(context)
                }).show();
        }
    }

    private fun openUserSerachFragment(username: String) {
        val userDetailFragment = UsersDetailsFragment()
        val args = Bundle()
        args.putString("username", username)
        userDetailFragment.setArguments(args)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, userDetailFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}