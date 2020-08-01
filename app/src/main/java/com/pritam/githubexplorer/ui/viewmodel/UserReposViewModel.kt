package com.pritam.githubexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pritam.githubexplorer.repository.GitRepository
import com.pritam.githubexplorer.retrofit.model.Resource
import kotlinx.coroutines.Dispatchers

class UserReposViewModel (private val gitRepository: GitRepository) : ViewModel() {

    fun getUserRepos(username: String, sort: String, per_page: Int, page: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = gitRepository.getUserRepos(username, sort, per_page, page)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}
