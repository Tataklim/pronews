package com.example.pronews.utils

import com.example.pronews.models.SingleNews
import java.io.Serializable

class SerializedSingleNews(elem: SingleNews) : Serializable {
    var author: String? = null
    var category: String? = null
    var country: String? = null
    var description: String? = null
    var image: String? = null
    var language: String? = null
    var published_at: String? = null
    var source: String? = null
    var title: String? = null
    var url: String? = null


    init {
        author = elem.author
        category = elem.category
        country = elem.country
        description = elem.description
        image = elem.image
        language = elem.language
        published_at = elem.published_at
        source = elem.source
        title = elem.title
        url = elem.url
    }
}