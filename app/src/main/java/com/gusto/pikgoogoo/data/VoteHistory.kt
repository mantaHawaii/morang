package com.gusto.pikgoogoo.data

data class VoteHistory(
    val id: Int,
    val voteCount: Int,
    val dateMilliSeconds: Long,
    var dateString: String = ""
)
