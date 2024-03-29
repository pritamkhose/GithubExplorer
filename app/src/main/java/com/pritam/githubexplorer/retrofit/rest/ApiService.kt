package com.pritam.githubexplorer.retrofit.rest

import com.pritam.githubexplorer.retrofit.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Github Doc - https://developer.github.com/v3/

interface ApiService {

    // https://api.github.com/users/pritamkhose
    @GET("users/{username}")
    suspend fun getUserDetails(@Path("username") username: String): UserDetailsResponse

    // https://api.github.com/search/users?q=pritamkhose&page=1
    @GET("search/users")
    suspend fun getUserSearch(
        @Query("q") username: String,
        @Query("page") page_no: Int
    ): UserSerachResponse

    //https://api.github.com/users/pritamkhose/repos?sort=updated&per_page=25
    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("sort") sort: String,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): ArrayList<UserReposResponse>

    // https://api.github.com/users/pritamkhose/followers
    @GET("users/{username}/followers?per_page=100")
    suspend fun getUserFollowers(@Path("username") username: String): List<UserFollowResponse>

    // https://api.github.com/users/pritamkhose/following
    @GET("users/{username}/following?per_page=100")
    suspend fun getUserFollowing(@Path("username") username: String): List<UserFollowResponse>

    // https://api.github.com/search/repositories?q=topic:android
    @GET("search/repositories")
    suspend fun getUserSearchDefault(@Query("q") query: String): UserSerachResponse


    // https://api.github.com/users/pritamkhose/gists
    @GET("users/{username}/gists?per_page=100&sort=updated")
    suspend fun getUserGist(@Path("username") username: String): List<UserGistResponse>
}