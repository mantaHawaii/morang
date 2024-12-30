package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReportReasonEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("reason")
    @Expose
    var reason:String

)
