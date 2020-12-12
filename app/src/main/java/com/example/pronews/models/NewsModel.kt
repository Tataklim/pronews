package com.example.pronews.models

import com.squareup.moshi.Json

data class NewsData(
        @Json(name = "data") val List: List<SingleNews>,
        @Json(name = "pagination") val Pagination: Pagination
)

data class SingleNews(
        var author: String?,
        var category: String?,
        var country: String?,
        var description: String?,
        var image: String?,
        var language: String?,
        var published_at: String?,
        var source: String?,
        var title: String?,
        var url: String?
)

data class Pagination(
        var count: String?,
        var limit: String?,
        var offset: String?,
        var total: String?
)