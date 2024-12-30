package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VoteHistoryEntity(

    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("voteCount")
    @Expose
    var voteCount:Int,

    @SerializedName("dateSeconds")
    @Expose
    var dateSeconds:Long

)
