package com.pritam.githubexplorer.retrofit.rest

import com.pritam.githubexplorer.BuildConfig
import com.pritam.githubexplorer.utils.Constants
import com.pritam.githubexplorer.utils.LogUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object ApiClient {

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)

    private fun getRetrofit(): Retrofit {

        val interceptor = HttpLoggingInterceptor()
        if(BuildConfig.DEBUG){
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()
                    val request: Request = original.newBuilder()
                        .header("Content-Type", "application/json; charset=utf-8")
                        .build()

                    val response: Response = chain.proceed(request)
                    when(response.code) {
                        401 -> {
                            LogUtils.error("Unauthorized-->", "$response")
                        }
                        400,403,404 -> {
                            LogUtils.error("Error--> ", "$response")
                        }
                        500,503 -> {
                            LogUtils.error("ServerError-->", "$response")
                        }
                    }
                    return response
                }
            })
            .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}

