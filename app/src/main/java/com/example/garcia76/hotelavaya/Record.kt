package com.example.garcia76.hotelavaya

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Record {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("nombre")
    @Expose
    var nombre: String? = null
    @SerializedName("apellidos")
    @Expose
    var apellidos: String? = null
    @SerializedName("hotel")
    @Expose
    var hotel: Int? = null
    @SerializedName("extension")
    @Expose
    var extension: String? = null
    @SerializedName("usuario")
    @Expose
    var usuario: String? = null
    @SerializedName("wifi")
    @Expose
    var wifi: String? = null
    @SerializedName("correo")
    @Expose
    var correo: String? = null
    @SerializedName("password")
    @Expose
    var password: String? = null
    @SerializedName("mac")
    @Expose
    var mac: String? = null

}