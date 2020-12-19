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

const val ACCESS_KEY = "b6d76b15107ddc1048ac8ccd464aa090";
const val ACCESS_KEY2 = "30610d1fee9dfebbf8ea6567fe65662d";

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