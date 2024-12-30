package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CategoryEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("categoryName")
    @Expose
    var name:String

)
