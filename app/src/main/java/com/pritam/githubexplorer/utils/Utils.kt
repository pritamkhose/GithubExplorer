package com.pritam.githubexplorer.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.extensions.replaceFragment
import java.util.regex.Pattern


// Add data to the intent, the receiving app will decide
fun Fragment.shareData(username: String, isGist: Boolean) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    share.putExtra(Intent.EXTRA_SUBJECT, "Share $username link!")
    if (isGist) {
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.gisturl) + username)
    } else {
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.giturl) + username)
    }
    startActivity(Intent.createChooser(share, "Share $username link!"))
}


fun Fragment.openCustomTabs(url: String) {
    if (url.length > 6 && url.contains("http")) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        context?.let { customTabsIntent.launchUrl(it, Uri.parse(url)) }
    }
}

fun Fragment.sendEmail(email: String, username: String) {
    if (email.length > 6 && isEmailValid(email)) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL, email)
        intent.putExtra(Intent.EXTRA_SUBJECT, context?.getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, "Hi $username,\n\nThanks & Regards,\n\n")
        startActivity(Intent.createChooser(intent, "Send Email!"))
    }
}

fun isEmailValid(email: String): Boolean {
    return Pattern.compile(
        Constants.EMAIL_VERIFICATION,
        Pattern.CASE_INSENSITIVE
    ).matcher(email).matches()
}

// when error came then show message
fun Fragment.snackBarError() {
    Snackbar.make(
        activity?.window?.decorView?.rootView!!,
        R.string.error,
        Snackbar.LENGTH_LONG
    ).show()
}

/**
 * Return current network connection status
 *
 * @param context Context
 * @return networkInfo?.isConnected Boolean
 */
fun Fragment.isNetworkAvailable(): Boolean {
    val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager) {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        networkInfo?.isConnected ?: false
    } else false
}

fun Fragment.openFragment(fragment: Fragment, username: String) {
    val args = Bundle()
    args.putString("username", username)
    fragment.arguments = args
    replaceFragment(fragment, R.id.fragment_container)
}
