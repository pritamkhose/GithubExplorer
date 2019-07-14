package com.pritam.githubexplorer.retrofit.rest

import com.pritam.githubexplorer.retrofit.model.UserDetailsResponse
import com.pritam.githubexplorer.retrofit.model.UserReposResponse
import com.pritam.githubexplorer.retrofit.model.UserSerachResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    // https://api.github.com/users/pritamkhose
    @GET("users/{username}")
    fun getUserDetails(@Path("username") username: String ): Call<UserDetailsResponse>

    // https://api.github.com/search/users?q=pritamkhose&page=1
    @GET("search/users")
    fun getUserSearch(@Query("q") username: String, @Query("pageno") id: Int ): Call<UserSerachResponse>

    //https://api.github.com/users/pritamkhose/repos
    @GET("users/{username}/repos")
    fun getUserRepos(@Path("username") username: String ): Call<UserReposResponse>

    // https://api.github.com/users/pritamkhose/followers
    @GET("users/{username}")
    fun getUserFollowers(@Path("username") username: String ): Call<UserDetailsResponse>

    // https://api.github.com/search/repositories?q=topic:android
    @GET("search/repositories")
    fun getUserSearchDefault(@Query("q") query: String ): Call<UserDetailsResponse>
}