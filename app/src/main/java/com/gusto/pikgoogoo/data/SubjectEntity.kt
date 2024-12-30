package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubjectEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("title")
    @Expose
    var title:String,

    @SerializedName("userId")
    @Expose
    var userId:Int,

    @SerializedName("nickname")
    @Expose
    var nickname:String,

    @SerializedName("grade")
    @Expose
    var grade:Int,

    @SerializedName("gradeIcon")
    @Expose
    var gradeIcon:Int,

    @SerializedName("voteCount")
    @Expose
    var voteCount:Int,

    @SerializedName("commentCount")
    @Expose
    var commentCount:Int,

    @SerializedName("createdDatetime")
    @Expose
    var createdDatetime:String,

    @SerializedName("categoryId")
    @Expose
    var categoryId:Int,

    @SerializedName("categoryName")
    @Expose
    var categoryName:String

)