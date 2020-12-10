package com.pritam.githubexplorer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pritam.githubexplorer.repository.GitRepository
import com.pritam.githubexplorer.ui.viewmodel.UserDetailsViewModel
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class UserDetailsViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()
    private val testUserName = "pritamkhose"

    private val repository = Mockito.mock(GitRepository::class.java)
    private var viewModel = UserDetailsViewModel(repository)

    @Test
    fun testNull() {
        MatcherAssert.assertThat(viewModel.getUserDetails(testUserName), CoreMatchers.notNullValue())
//        Mockito.verify(repository, Mockito.never()).getUserDetails(Mockito.anyString(), Mockito.anyString())
    }

}