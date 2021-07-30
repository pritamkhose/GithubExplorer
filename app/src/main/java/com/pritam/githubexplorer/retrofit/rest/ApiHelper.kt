package com.pritam.githubexplorer.retrofit.rest

class ApiHelper(private val apiService: ApiService) {

    suspend fun getUserDetails(username: String) = apiService.getUserDetails(username)

    suspend fun getUserSearch(username: String, page_no: Int) =
        apiService.getUserSearch(username, page_no)

    suspend fun getUserRepos(username: String, sort: String, per_page: Int, page: Int) =
        apiService.getUserRepos(username, sort, per_page, page)

    suspend fun getUserFollower(username: String) = apiService.getUserFollowers(username)

    suspend fun getUserFollowing(username: String) = apiService.getUserFollowing(username)

    suspend fun getUserGist(username: String) = apiService.getUserGist(username)

}