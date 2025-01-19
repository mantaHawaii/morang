package com.gusto.pikgoogoo.data

import android.net.Uri

data class Article(
    var rank:Int = 0,
    val id: Int,
    val content: String,
    val subjectId: Int,
    val imageUrl: String,
    var voteCount: Int,
    var commentCount: Int,
    val createdDatetime:String,
    var totalVotesInPeriod: Int,
    val cropImage: Int,
    var imageUri: Uri? = null,
    var voteRate: Float = 0.0f
)
