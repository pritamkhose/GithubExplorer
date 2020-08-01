package com.pritam.githubexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pritam.githubexplorer.repository.GitRepository
import com.pritam.githubexplorer.retrofit.model.Resource
import kotlinx.coroutines.Dispatchers

class UserSearchViewModel(private val gitRepository: GitRepository) : ViewModel() {

    fun getUsers(username: String, page_no: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = gitRepository.getUserSearch(username, page_no)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}