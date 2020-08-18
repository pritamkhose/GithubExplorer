package com.pritam.githubexplorer.ui.fragment

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.utils.Constants

open class BaseFragment : Fragment() {

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.window?.decorView?.rootView!!.windowToken, 0)
    }

    open fun handleError(t: Throwable) {
        Log.e(Constants.APP_TAG, t.toString())
        showSnackMsg(R.string.error)
    }

    fun showSnackMsg(msg: Int) {
        Snackbar.make(
            activity?.window?.decorView?.rootView!!,
            msg,
            Snackbar.LENGTH_LONG
        ).show()
    }

    /*fun noNetWork(context: Context) {
    // network is not present then show message
    Snackbar.make(
        activity?.window?.decorView?.rootView!!,
        R.string.network_error,
        Snackbar.LENGTH_LONG
    )
        .setAction("Retry") {
//                UserSerachFragment.
        }.show()
}*/

}
