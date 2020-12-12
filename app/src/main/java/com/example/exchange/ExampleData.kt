package com.example.exchange

import com.squareup.moshi.Json

data class ExampleData(
        val Response: String,
        val Message: String,
        val HasWarning: Boolean,
        val Type: Int,
        @Json(name = "Data") val Data: Data
)

data class Data(
        @Json(name = "Data") val List: List<DayInfo>
)

data class DayInfo(
        var time: String,
        var high: String,
        var low: String,
        var open: String,
        var close: String
)