package com.pritam.githubexplorer.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pritam.githubexplorer.repository.GitRepository
import com.pritam.githubexplorer.retrofit.rest.ApiHelper
import com.pritam.githubexplorer.ui.viewmodel.*

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserSearchViewModel::class.java)) {
            return UserSearchViewModel(GitRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
            return UserDetailsViewModel(GitRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(UserFollowerViewModel::class.java)) {
            return UserFollowerViewModel(GitRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(UserFollowingViewModel::class.java)) {
            return UserFollowingViewModel(GitRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(UserReposViewModel::class.java)) {
            return UserReposViewModel(GitRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(UserGistViewModel::class.java)) {
            return UserGistViewModel(GitRepository(apiHelper)) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }
}

