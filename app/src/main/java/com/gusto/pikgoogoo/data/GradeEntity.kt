package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GradeEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("minValue")
    @Expose
    var minValue: Int

)
