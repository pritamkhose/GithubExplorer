package com.pritam.githubexplorer

import com.pritam.githubexplorer.retrofit.rest.ApiService
import com.pritam.githubexplorer.utils.Constants
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class GithubServiceTest {

    private lateinit var service: ApiService
    private val testUserName = "pritamkhose"

    @Before
    fun createService() {
        // call the api
        service = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Test
    fun `User Details correct` () = runBlocking {
        val response = service.getUserDetails(testUserName)
        // verify the response is OK
        println(response)
        assertNotEquals(response, null)
        assertNotEquals(response, "")
        assertEquals(response.name, "Pritam Khose")
    }

    @Test
    fun `User Search correct` () = runBlocking {
        val response = service.getUserSearch(testUserName, 1)
        assertNotEquals(response, null)
        assertNotEquals(response, "")
        assertNotEquals(response.items.size, 0)
        assertNotEquals(response.items[0].login, null)
    }

    @Test
    fun `User Search not correct` () = runBlocking {
        val response = service.getUserSearch(testUserName+"12345", 1)
        assertEquals(response.items.size, 0)
        assertEquals(response.total_count, 0)
    }

    @Test
    fun `User Repos correct` () = runBlocking {
        val response = service.getUserRepos(testUserName, "updated", 25, 1)
        assertNotEquals(response, null)
        assertNotEquals(response, "")
        assertNotEquals(response.size, 0)
        assertNotEquals(response[0].name, null)
        assertEquals(response[0].owner.login, testUserName)
    }


    @Test
    fun `User Followers correct` () = runBlocking {
        val response = service.getUserFollowers(testUserName)
        assertNotEquals(response, null)
        assertNotEquals(response, "")
        assertNotEquals(response.size, 0)
        assertNotEquals(response[0].login, null)
    }

    @Test
    fun `User Following correct` () = runBlocking {
        val response = service.getUserFollowing(testUserName)
        assertNotEquals(response, null)
        assertNotEquals(response, "")
        assertNotEquals(response.size, 0)
        assertNotEquals(response[0].login, null)
    }
    

    @Test
    fun `User Search Default repositories correct` () = runBlocking {
        val response = service.getUserSearchDefault("Android")
        assertNotEquals(response.total_count, 0)
        assertEquals(response.incomplete_results, false)
    }

}