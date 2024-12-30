package com.gusto.pikgoogoo.data

data class Subject(
    val id: Int,
    val title: String,
    val userId: Int,
    val userNicknmae: String,
    val userGrade: Int,
    val userGradeIcon: Int,
    val commentCount: Int,
    val voteCount: Int,
    val createdDatetime:String,
    val categoryId:Int,
    val categoryName:String
)
