package com.example.exchange

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/v2/histoday")
    fun search(
        @Query("fsym") crypto: String,
        @Query("tsym") currency: String,
        @Query("limit") period: String
    ): Observable<ExampleData>

    companion object Factory {
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl("https://min-api.cryptocompare.com/")
                .build()

            return retrofit.create(ApiService::class.java);
        }
    }
}