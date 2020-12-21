package com.example.pronews.utils

import com.example.pronews.models.SingleNews

object NewsDataDB {
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
}