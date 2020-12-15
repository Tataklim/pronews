package com.example.pronews.utils

import android.util.Log
import com.example.pronews.models.SingleNews
import com.example.pronews.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object NewsData {
    private var newsSet: MutableList<SingleNews> = mutableListOf()

    fun checkIfEmpty(): Boolean {
        return newsSet.isEmpty();
    }

    fun getData(): MutableList<SingleNews> {
        return newsSet;
    }

    fun update(category: String, country: String, callback: (m: MutableList<SingleNews>) -> Unit) {
        Log.v("KEK kek", "update")
        newsSet.clear()
        val temp = ApiService.create()
        temp.news(category, country)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                result.List.map { elem -> newsSet.add(elem) }
                callback(newsSet)
            }, { error ->
                error.message?.let { Log.v("Error", it) }
                error.printStackTrace()
            })
    }
}