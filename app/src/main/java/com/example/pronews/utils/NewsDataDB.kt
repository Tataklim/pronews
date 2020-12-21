package com.example.pronews.utils

import com.example.pronews.db.NewsItem
import com.example.pronews.models.SingleNews

object NewsDataDB {
    private var newsSet: MutableList<SingleNews> = mutableListOf()

    fun checkIfEmpty(): Boolean {
        return newsSet.isEmpty();
    }

    fun getData(): MutableList<SingleNews> {
        return newsSet;
    }

    fun setData(data: MutableList<NewsItem>) {
        newsSet.clear()
        data.map { elem ->
            val singleNews = SingleNews(
                elem.author,
                elem.category,
                elem.country,
                elem.description,
                elem.image,
                elem.language,
                elem.published_at,
                elem.source,
                elem.title,
                elem.url
            )
            newsSet.add(singleNews)
        }
    }
}