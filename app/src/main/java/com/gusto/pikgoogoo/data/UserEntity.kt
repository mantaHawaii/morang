package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("nickname")
    @Expose
    var nickname:String,

    @SerializedName("grade")
    @Expose
    var grade:Int,

    @SerializedName("gradeIcon")
    @Expose
    var gradeIcon:Int,

    @SerializedName("isRestricted")
    @Expose
    var isRestricted:Int

)
