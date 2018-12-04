package com.example.garcia76.hotelavaya.DataClass

data class LoginDataClass(
        var records: List<Record> = listOf()
) {
    data class Record(
            var apellidos: String = "",
            var correo: String = "",
            var extension: String = "",
            var hotel: Int = 0,
            var id: Int = 0,
            var idiomatts: String = "",
            var mac: String = "",
            var nombre: String = "",
            var password: String = "",
            var usuario: String = "",
            var wifi: String = ""
    )
}