package com.example.pronews.db

import com.example.pronews.models.SingleNews

class NewsItem {
    var _id: String? = null
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

    constructor() {}

    constructor(elem: SingleNews) {
        this.author = elem.author
        this.category = elem.category
        this.country = elem.country
        this.description = elem.description
        this.image = elem.image
        this.language = elem.language
        this.published_at = elem.published_at
        this.source = elem.source
        this.title = elem.title
        this.url = elem.url
    }
}