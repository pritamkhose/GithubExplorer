package com.pritam.githubexplorer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pritam.githubexplorer.retrofit.rest.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class GithubServiceTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: ApiService
    private val testUserName = "pritamkhose"
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        // call the api
        service = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
        mockWebServer = MockWebServer()
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

//    @Test
//    fun `User Details 1 correct`() = runBlocking {
//        enqueueResponse("users-pritamkhose.json")
//        val request = mockWebServer.takeRequest()
//        println(request.toString())
//
//        val response = service.getUserDetails(testUserName)
//        // verify the response is OK
//        println(response)
//        assertNotEquals(response, null)
//        assertNotEquals(response, "")
//        assertEquals(response.name, "Pritam Khose")
//    }

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

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
            .getResourceAsStream("api-response/$fileName")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }

}