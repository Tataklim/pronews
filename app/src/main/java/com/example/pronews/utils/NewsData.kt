package com.example.pronews.utils

import android.annotation.SuppressLint
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

    @SuppressLint("CheckResult")
    fun update(category: String, language: String, callback: (m: MutableList<SingleNews>) -> Unit) {
        newsSet.clear()
        Log.v("KEK kek", "update" + newsSet.size)
        val temp = ApiService.create()
        temp.news()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                result.List.map { elem -> newsSet.add(elem) }
                Log.v("KEK size lol", "NO ERROR")
                callback(newsSet)
            }, { error ->
                error.message?.let { Log.v("Error", it) }
                Log.v("KEK size lol", "ERROR")
                error.printStackTrace()
            })
    }
}