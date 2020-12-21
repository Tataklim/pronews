package com.example.pronews.utils

import android.annotation.SuppressLint
import android.util.Log
import com.example.pronews.models.NewsData
import com.example.pronews.models.SingleNews
import com.example.pronews.network.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object NewsData {
    private var newsSet: MutableList<SingleNews> = mutableListOf()
    var newsSetWorker: MutableList<SingleNews> = mutableListOf()
    private var newsTitles: MutableSet<String?> = mutableSetOf()

    fun checkIfEmpty(): Boolean {
        return newsSet.isEmpty();
    }

    fun getData(): MutableList<SingleNews> {
        return newsSet;
    }

    fun changeData() { // Не удолять
        newsSet.clear()
        newsSetWorker.map { elem ->
            newsSet.add(elem)
        }
    }

    @SuppressLint("CheckResult")
    fun update(category: String, language: String, callback: (m: MutableList<SingleNews>) -> Unit) {
        newsSet.clear()
        newsTitles.clear()
        val temp = ApiService.create()
        temp.news(category, language)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                result.List.map { elem ->
                    newsSet.add(elem)
                }
                Log.v("KEK size lol", newsSet.size.toString())
                callback(newsSet)
            }, { error ->
                error.message?.let { Log.v("Error", it) }
                Log.v("KEK size lol", "ERROR")
                error.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    fun updateWorker(category: String, language: String): Observable<NewsData> {
        newsSetWorker.clear()
        val temp = ApiService.create()
        return temp.news(category, language)
    }
}