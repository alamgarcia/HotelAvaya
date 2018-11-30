package com.example.garcia76.hotelavaya.DataClass

data class LanguageData(
        var `data`: Data = Data()
) {
    data class Data(
            var languages: List<Language> = listOf()
    ) {
        data class Language(
                var language: String = "",
                var name: String = ""
        )
    }
}