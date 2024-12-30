package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommentEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("comment")
    @Expose
    var comment:String,

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

    @SerializedName("subjectId")
    @Expose
    var subjectId:Int,

    @SerializedName("articleId")
    @Expose
    var articleId:Int,

    @SerializedName("likeCount")
    @Expose
    var likeCount:Int,

    @SerializedName("createdDatetime")
    @Expose
    var createdDatetime:String,

    @SerializedName("content")
    @Expose
    var content:String = ""

)
