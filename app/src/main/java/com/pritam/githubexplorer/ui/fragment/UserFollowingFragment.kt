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
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentFollowBinding
import com.pritam.githubexplorer.extensions.replaceFragment
import com.pritam.githubexplorer.retrofit.model.UserFollowResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserFollowListAdapter
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.pritam.githubexplorer.utils.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class UserFollowingFragment : Fragment() {

    private lateinit var mBinding: FragmentFollowBinding
    private val compositeDisposable = CompositeDisposable()
    private var username = ""
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
        activity?.title = username.toUpperCase(Locale.ROOT) + " Following"

        //Add a LayoutManager
        mBinding.recyclerViewFollow.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)

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
            compositeDisposable.add(
                ApiClient.client.getUserFollowing(username)
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
                    fetchFollowing(context)
                }.show()
        }
    }

    private fun handleResults(aObj: List<UserFollowResponse>) {
        try {
            if (aObj.isNotEmpty()) {
                aListFollow = aObj
                mBinding.recyclerViewFollow.adapter = UserFollowListAdapter(aListFollow)
            } else {
                Snackbar.make(
                    activity?.window?.decorView?.rootView!!,
                    R.string.nouser,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(
                activity?.window?.decorView?.rootView!!,
                R.string.error,
                Snackbar.LENGTH_LONG
            )
                .show()
        }
    }

    private fun handleError(t: Throwable) {
        Log.e(Constants.APP_TAG, t.toString())
        Snackbar.make(activity?.window?.decorView?.rootView!!, R.string.error, Snackbar.LENGTH_LONG)
            .show()
    }


    private fun openUserSerachFragment(username: String) {
        val fragment = UsersDetailsFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        replaceFragment(fragment, R.id.fragment_container)
    }

}