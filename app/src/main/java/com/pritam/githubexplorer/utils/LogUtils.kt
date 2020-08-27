package com.pritam.githubexplorer.utils

import android.util.Log
import com.pritam.githubexplorer.BuildConfig

class LogUtils {
    companion object {
        fun debug(tag: String, msg: String) {
            if(BuildConfig.LOGGER){
                Log.d(tag, msg)
            }
        }

        fun error(tag: String, msg: String) {
            if(BuildConfig.LOGGER){
                Log.e(tag, msg)
            }
        }
    }
}