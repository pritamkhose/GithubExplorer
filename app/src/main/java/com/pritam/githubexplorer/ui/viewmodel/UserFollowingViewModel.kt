package com.pritam.githubexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pritam.githubexplorer.repository.GitRepository
import com.pritam.githubexplorer.retrofit.model.Resource
import kotlinx.coroutines.Dispatchers

class UserFollowingViewModel (private val gitRepository: GitRepository) : ViewModel() {

    fun getUserFollowing(username: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = gitRepository.getUserFollowing(username)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}