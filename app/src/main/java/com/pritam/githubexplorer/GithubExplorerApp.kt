package com.pritam.githubexplorer

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class GithubExplorerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}