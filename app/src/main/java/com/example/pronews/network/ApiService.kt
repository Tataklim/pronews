package com.example.pronews.network

import com.example.pronews.models.NewsData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val ACCESS_KEY = "74b2241eae6a0a239529f18fcd4d479a"; // e6f90c1cb673b8bc7cff53c0a5699267

interface ApiService {

    @GET("news")
    fun news(
        @Query("categories") lang: String = "",
        @Query("languages") language: String = "",
        @Query("access_key") access_key: String = ACCESS_KEY
    ): Observable<NewsData>

    companion object Factory {
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl("http://api.mediastack.com/v1/")
                .build()

            return retrofit.create(ApiService::class.java);
        }
    }
}