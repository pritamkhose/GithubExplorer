package com.pritam.githubexplorer.retrofit.rest

import com.pritam.githubexplorer.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit

object ApiClient {

    private var retrofit: Retrofit? = null

    val client: Retrofit?
        get() {
            if (retrofit === null) {

                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .build()


                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            }
            return retrofit
        }
}