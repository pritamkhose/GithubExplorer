package com.pritam.githubexplorer.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.retrofit.model.Item
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.utils.ConnectivityUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pritam.githubexplorer.ui.base.ViewModelFactory
import com.pritam.githubexplorer.databinding.FragmentUserSearchBinding
import com.pritam.githubexplorer.retrofit.model.Status
import com.pritam.githubexplorer.retrofit.model.UserSerachResponse
import com.pritam.githubexplorer.retrofit.rest.ApiHelper
import com.pritam.githubexplorer.ui.adapter.EndlessRecyclerOnScrollListener
import com.pritam.githubexplorer.ui.adapter.UserSearchListAdapter
import com.pritam.githubexplorer.ui.viewmodel.UserSearchViewModel
import com.pritam.githubexplorer.utils.LogUtils

class UserSearchFragment : Fragment() {

    private val mTAG = UserSearchFragment::class.java.simpleName
    private lateinit var mBinding: FragmentUserSearchBinding
    private lateinit var viewModel: UserSearchViewModel
    private lateinit var adapter: UserSearchListAdapter
    private var textSearchStr = ""
    private var lastTextSearchStr = "*"
    private var pageno = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            textSearchStr = it.getString("username", "")
        }
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Define the listener for binding
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_search, container, false)

        setupUI()
        setupObservers()

        return mBinding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiClient.apiService))
        ).get(UserSearchViewModel::class.java)
    }

    @SuppressLint("WrongConstant")
    private fun setupUI() {
        val context = activity as Context
        activity?.setTitle(R.string.app_name)

        // Search enter text
        mBinding.textSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mBinding.textSearch.text != null) {
                    hideKeyboard()
                    textSearchStr = mBinding.textSearch.text.toString()
                    setupObservers()
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
        if (textSearchStr.isNotEmpty()) {
            mBinding.textSearch.setText(textSearchStr)
        }
        // Search box
        // get reference to ImageView
        mBinding.imSearch.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            if (mBinding.textSearch.text != null) {
                hideKeyboard()
                textSearchStr = mBinding.textSearch.text.toString()
                setupObservers()
            } else {
                Snackbar.make(
                    activity?.window?.decorView?.rootView!!,
                    R.string.enterusername,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        //Connect adapter with recyclerView
        adapter = UserSearchListAdapter(arrayListOf())
        mBinding.recyclerView.adapter = adapter

        //Add a LayoutManager
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        mBinding.recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                mBinding.recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        openFragment(adapter.getItem(position).login)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )

        mBinding.recyclerView.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                pageno += 1
                LogUtils.debug(mTAG, "onLoadMore $pageno")
                setupObservers()
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
            setupObservers()
        }
        pageno = 1
    }

    private fun setupObservers() {
        if (textSearchStr.isEmpty()) {
            textSearchStr = "android"
        }
        if (lastTextSearchStr != textSearchStr) {
            lastTextSearchStr = textSearchStr
            pageno = 1
        }
        LogUtils.debug(mTAG, pageno.toString())
        if (activity?.baseContext?.let { ConnectivityUtils.isNetworkAvailable(it) }!!) {
            viewModel.getUsers(textSearchStr, pageno).observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            mBinding.swipeRefreshLayout.isRefreshing = false
                            resource.data?.let { users -> retrieveList(users) }
                        }
                        Status.ERROR -> {
                            if (pageno > 1){
                                pageno-= 1
                            }
                            mBinding.swipeRefreshLayout.isRefreshing = false
                            Snackbar.make(
                                activity?.window?.decorView?.rootView!!,
                                R.string.error,
                                Snackbar.LENGTH_LONG
                            ).show()
                            LogUtils.debug(mTAG, it.message.toString())
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

    private fun retrieveList(res: UserSerachResponse) {
        try {
            if (res.total_count > 0) {
                val users: List<Item> = res.items
                adapter.apply {
                    if (pageno == 1) {
                        clearItem()
                    }
                    addItem(users)
                    notifyDataSetChanged()
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
