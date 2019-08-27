package com.pritam.githubexplorer.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
    }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

// https://medium.com/@niazi.abdulaziz/playing-with-kotlin-extensions-2-the-fragment-manager-class-f0006c451379
// https://github.com/mgarciaguerrero/safe-android-fragments
inline fun Fragment.replaceFragment(fragment: Fragment, frameId: Int) {
    fragmentManager!!.inTransaction {
        replace(frameId, fragment, fragment.javaClass.simpleName)
        addToBackStack(null)
    }
}
