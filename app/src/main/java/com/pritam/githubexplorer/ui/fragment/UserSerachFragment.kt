package com.pritam.githubexplorer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.FragmentUserSearchBinding
import com.pritam.githubexplorer.retrofit.model.Item
import com.pritam.githubexplorer.retrofit.model.UserSerachResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.ui.adapter.EndlessRecyclerOnScrollListener
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserSerachListAdapter
import com.pritam.githubexplorer.utils.ConnectivityUtils
import com.pritam.githubexplorer.utils.Constants.Companion.APP_TAG
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class UserSerachFragment : Fragment() {

    private val TAG = UserSerachFragment::class.java.simpleName
    private lateinit var mBinding: FragmentUserSearchBinding
    private var aList: ArrayList<Item> = ArrayList()
    private var textSearchStr = ""
    private var lastTextSearchStr = "*"
    private var pageno = 1
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            textSearchStr = it.getString("username", "")
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Define the listener for binding
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_search, container, false)


        val context = activity as Context
        activity?.setTitle(R.string.app_name)

        // Search enter text
        mBinding.textSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mBinding.textSearch.text != null) {
                    hideKeyboard()
                    textSearchStr = mBinding.textSearch.text.toString()
                    fetchdata(context)
                } else {
                    Snackbar.make(
                        activity?.window?.decorView?.rootView!!,
                        R.string.enterusername,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                true
            } else {
                false
            }
        }
        if (textSearchStr.length > 0) {
            mBinding.textSearch.setText(textSearchStr)
        }
        // Search box
        // get reference to ImageView
        mBinding.imSearch.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            if (mBinding.textSearch.text != null) {
                hideKeyboard()
                textSearchStr = mBinding.textSearch.text.toString()
                fetchdata(context)
            } else {
                Snackbar.make(
                    activity?.window?.decorView?.rootView!!,
                    R.string.enterusername,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        //Add a LayoutManager
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        mBinding.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                mBinding.recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        openFragment(aList[position].login)
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
        pageno = 1
        fetchdata(context)

        return mBinding.root
    }

    private fun fetchdata(context: Context) {
        if (textSearchStr.isEmpty()) {
            textSearchStr = "android"
        }
        if (lastTextSearchStr != textSearchStr) {
            lastTextSearchStr = textSearchStr
            pageno = 1
        }
        Log.d(TAG, pageno.toString())
        if (ConnectivityUtils.isNetworkAvailable(context)) {
            if (!mBinding.swipeRefreshLayout.isRefreshing) {
                // Show swipe to refresh icon animation
                mBinding.swipeRefreshLayout.isRefreshing = true
                // network is present so will load updated data
                compositeDisposable.add(
                    ApiClient.client.getUserSearch(textSearchStr, pageno)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResults, this::handleError)
                )
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

    private fun handleResults(aObj: UserSerachResponse) {
        // Hide swipe to refresh icon animation
        mBinding.swipeRefreshLayout.isRefreshing = false
        try {
            if (aObj.total_count > 0) {
                val alList = aObj.items as ArrayList<Item>
                //creating adapter and item adding to adapter of recyclerview
                if (pageno == 1) {
                    aList = alList
                    mBinding.recyclerView.adapter = UserSerachListAdapter(aList)
                } else {
                    aList.addAll(alList)
                    (mBinding.recyclerView.adapter as UserSerachListAdapter).notifyDataSetChanged()
                }
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
            ).show()
        }
    }

    private fun handleError(t: Throwable) {
        // Hide swipe to refresh icon animation
        mBinding.swipeRefreshLayout.isRefreshing = false
        Log.e(APP_TAG, t.toString())
        Snackbar.make(activity?.window?.decorView?.rootView!!, R.string.error, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun openFragment(username: String) {
        val userDetailFragment = UsersDetailsFragment()
        val args = Bundle()
        args.putString("username", username)
        userDetailFragment.arguments = args
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, userDetailFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.window?.decorView?.rootView!!.windowToken, 0)
    }
}
