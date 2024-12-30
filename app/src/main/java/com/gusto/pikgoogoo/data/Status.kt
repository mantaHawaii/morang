package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("code")
    @Expose
    var code: String,

    @SerializedName("message")
    @Expose
    var message: String

)
