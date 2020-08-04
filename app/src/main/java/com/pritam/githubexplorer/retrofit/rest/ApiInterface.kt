package com.pritam.githubexplorer.retrofit.rest

import com.pritam.githubexplorer.retrofit.model.UserDetailsResponse
import com.pritam.githubexplorer.retrofit.model.UserFollowResponse
import com.pritam.githubexplorer.retrofit.model.UserReposResponse
import com.pritam.githubexplorer.retrofit.model.UserSerachResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Github Doc - https://developer.github.com/v3/

interface ApiInterface {

    // https://api.github.com/users/pritamkhose
    @GET("users/{username}")
    fun getUserDetails(@Path("username") username: String): Observable<UserDetailsResponse>

    // https://api.github.com/search/users?q=pritamkhose&page=1
    @GET("search/users")
    fun getUserSearch(
        @Query("q") username: String,
        @Query("page") pageno: Int
    ): Observable<UserSerachResponse>

    //https://api.github.com/users/pritamkhose/repos?sort=updated&per_page=25
    @GET("users/{username}/repos")
    fun getUserRepos(
        @Path("username") username: String,
        @Query("sort") sort: String,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): Observable<ArrayList<UserReposResponse>>

    // https://api.github.com/users/pritamkhose/followers
    @GET("users/{username}/followers")
    fun getUserFollowers(@Path("username") username: String): Observable<List<UserFollowResponse>>

    // https://api.github.com/users/pritamkhose/following
    @GET("users/{username}/following")
    fun getUserFollowing(@Path("username") username: String): Observable<List<UserFollowResponse>>

    // https://api.github.com/search/repositories?q=topic:android
    @GET("search/repositories")
    fun getUserSearchDefault(@Query("q") query: String): Observable<UserDetailsResponse>
}