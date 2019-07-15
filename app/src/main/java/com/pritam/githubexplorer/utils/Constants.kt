package com.pritam.githubexplorer.utils

public class Constants {

    // static constants
    companion object {
        const val BASE_URL: String = "https://api.github.com/"
        const val APP_TAG: String = "GithubExplorer"
        const val EMAIL_VERIFICATION = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$"
        const val CONNECTION_TIMEOUT: Long = 30
    }
}