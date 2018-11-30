package com.example.garcia76.hotelavaya.DataClass

data class UsersData(
        var records: List<Record> = listOf()
) {
    data class Record(
            var id: Int = 0,
            var nombre: String = ""
    )
}