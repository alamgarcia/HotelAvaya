package com.example.garcia76.hotelavaya

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import android.icu.text.AlphabeticIndex.Record


class LoginData {

    @SerializedName("records")
    @Expose
    var records: List<Record<*>>? = null

}
