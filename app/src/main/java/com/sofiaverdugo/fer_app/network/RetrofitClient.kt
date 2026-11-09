package com.sofiaverdugo.fer_app.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    const val HOST = "http://10.0.2.2:1337"
}

object RetrofitClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("${ApiConfig.HOST}/api/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val postsApi: PostsAPI by lazy { retrofit.create(PostsAPI::class.java) }
    val eventsApi: EventsAPI by lazy { retrofit.create(EventsAPI::class.java) }
}