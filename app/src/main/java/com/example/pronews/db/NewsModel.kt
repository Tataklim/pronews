package com.example.pronews.db

import android.provider.BaseColumns

object NewsModel {
    internal var TABLE_USER = "news"

    internal class NewsColumns : BaseColumns {
        companion object {
            var AUTHOR = "author"
            var CATEGORY = "category"
            var COUNTRY = "country"
            var DESCRIPRTION = "description"
            var IMAGE = "image"
            var LANGUAGE = "language"
            var PUBLISHED_AT = "published_at"
            var SOURCE = "source"
            var TITLE = "title"
            var URL = "url"
        }

    }
}