package com.gusto.pikgoogoo.data

data class Comment(
    val id: Int,
    var comment: String,
    val userId: Int,
    val nickname: String,
    val grade: Int,
    val gradeIcon: Int,
    val subjectId: Int,
    val articleId: Int,
    var likeCount: Int,
    val createdDatetime:String,
    val content: String = ""
)
