package com.example.garcia76.hotelavaya.DataClass

data class TranslateData(
        var `data`: Data = Data()
) {
    data class Data(
            var translations: List<Translation> = listOf()
    ) {
        data class Translation(
                var detectedSourceLanguage: String = "",
                var translatedText: String = ""
        )
    }
}