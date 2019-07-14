package com.pritam.githubexplorer.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.retrofit.model.Item
import com.pritam.githubexplorer.retrofit.model.UserSerachResponse
import com.pritam.githubexplorer.retrofit.rest.ApiClient
import com.pritam.githubexplorer.retrofit.rest.ApiInterface
import com.pritam.githubexplorer.ui.adapter.RecyclerTouchListener
import com.pritam.githubexplorer.ui.adapter.UserSerachListAdapter
import com.pritam.githubexplorer.utils.ConnectivityUtils
import kotlinx.android.synthetic.main.fragment_user_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.EditText

class UserSerachFragment : Fragment() {

    private val TAG = UsesDetailsFragment::class.java.simpleName
    private lateinit var rootView: View
    private var aList: ArrayList<Item> = ArrayList()
    private var textSearch = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            textSearch = it.getString("username", "android")
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_user_search, container, false)

        val context = activity as Context
        getActivity()?.setTitle(R.string.app_name);


        // Search box
        val edSearch: (EditText) = rootView.findViewById(R.id.textSearch)
        edSearch.setOnEditorActionListener { v, actionId, _event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if(edSearch.text != null){
                    hideKeyboard()
                    fetchdata( edSearch.text.toString(), 1)
                } else {
                    Snackbar.make(rootView, R.string.enterusername, Snackbar.LENGTH_LONG).show();
                }
                true
            } else {
                false
            }
        }


        //Bind the recyclerview
        val recyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView

        //Add a LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        swapFragment(aList[position].login)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )

        if (ConnectivityUtils.isNetworkAvailable(context)) {
            // network is present so will load updated data
            fetchdata("android", 1)
        } else {
            // network is not present then show message
            Snackbar.make(rootView, R.string.network_error, Snackbar.LENGTH_LONG).show();
        }
        return rootView
    }

    private fun fetchdata(uname: String, pageno: Int) {
        val apiService = ApiClient.client!!.create(ApiInterface::class.java)

        val call = apiService.getUserSearch(uname, pageno)
        call.enqueue(object : Callback<UserSerachResponse> {
            override fun onResponse(call: Call<UserSerachResponse>, response: Response<UserSerachResponse>) {
                val aObj: UserSerachResponse? = response.body()
                Log.d(TAG, "Response " + aObj.toString())
                if (aObj != null) {
                    if (aObj.total_count > 0) {
                        aList = aObj.items as ArrayList<Item>
                        //creating adapter and item adding to adapter of recyclerview
                        recyclerView.adapter = UserSerachListAdapter(aList);
                        (recyclerView.adapter as UserSerachListAdapter).notifyDataSetChanged();
                    } else {
                        Snackbar.make(rootView, R.string.nouser, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(rootView, R.string.error, Snackbar.LENGTH_LONG).show();
                }
            }

            override fun onFailure(call: Call<UserSerachResponse>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
            }
        })
    }


    private fun swapFragment(username: String) {
        val userDetailFragment = UsesDetailsFragment()
        val args = Bundle()
        args.putString("username", username)
        userDetailFragment.setArguments(args)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, userDetailFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun hideKeyboard() {
        val imm = getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }
}
