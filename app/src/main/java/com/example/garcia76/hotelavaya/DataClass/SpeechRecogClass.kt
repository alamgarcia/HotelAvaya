package com.example.garcia76.hotelavaya.DataClass

import com.google.gson.annotations.SerializedName

data class SpeechRecogClass(
        var records: List<Record> = listOf()
) {
    data class Record(
            var id: Int = 0,
            var lang: String = "",
            @SerializedName("lang_us")
            var langUs: String = "",
            var langcode: String = ""
    )
}