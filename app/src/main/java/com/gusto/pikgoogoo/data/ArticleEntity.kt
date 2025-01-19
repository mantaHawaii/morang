package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ArticleEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("content")
    @Expose
    var content:String,

    @SerializedName("subjectId")
    @Expose
    var subjectId:Int,

    @SerializedName("imageUrl")
    @Expose
    var imageUrl:String,

    @SerializedName("voteCount")
    @Expose
    var voteCount:Int,

    @SerializedName("commentCount")
    @Expose
    var commentCount:Int,

    @SerializedName("createdDatetime")
    @Expose
    var createdDatetime:String,

    @SerializedName("totalVotesInPeriod")
    @Expose
    var totalVotesInPeriod:Int,

    @SerializedName("cropImage")
    @Expose
    var cropImage:Int

)
