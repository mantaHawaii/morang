package com.gusto.pikgoogoo.data

data class User(
    val id: Int,
    val nickname: String,
    val grade: Int,
    var gradeIcon: Int,
    val isRestricted: Int
)
