package com.pritam.githubexplorer.repository

import com.pritam.githubexplorer.retrofit.rest.ApiHelper

class GitRepository(private val apiHelper: ApiHelper) {

    suspend fun getUserDetails(username: String) = apiHelper.getUserDetails(username)

    suspend fun getUserSearch(username: String, page_no: Int) =
        apiHelper.getUserSearch(username, page_no)

    suspend fun getUserRepos(username: String, sort: String, per_page: Int, page: Int) =
        apiHelper.getUserRepos(username, sort, per_page, page)

    suspend fun getUserFollower(username: String) = apiHelper.getUserFollower(username)

    suspend fun getUserFollowing(username: String) = apiHelper.getUserFollowing(username)

    suspend fun getUserGist(username: String) = apiHelper.getUserGist(username)
}
