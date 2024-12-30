package com.gusto.pikgoogoo.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Response(

    @SerializedName("status")
    @Expose
    var status: Status,

    @SerializedName("user")
    @Expose
    var user: UserEntity,

    @SerializedName("categories")
    @Expose
    var categories: List<CategoryEntity>,

    @SerializedName("subjects")
    @Expose
    var subjects: List<SubjectEntity>,

    @SerializedName("articles")
    @Expose
    var articles: List<ArticleEntity>,

    @SerializedName("articleCreatedDate")
    @Expose
    var articleCreatedDate: String,

    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("dbVersion")
    @Expose
    var dbVersion: Int,

    @SerializedName("title")
    @Expose
    var title: String,

    @SerializedName("totalVoteCount")
    @Expose
    var totalVoteCount: Int,

    @SerializedName("voteHistoryData")
    @Expose
    var voteHistoryData: List<VoteHistoryEntity>,

    @SerializedName("comments")
    @Expose
    var comments: List<CommentEntity>,

    @SerializedName("grade")
    @Expose
    var grade: List<GradeEntity>,

    @SerializedName("reportReasons")
    @Expose
    var reportReasons: List<ReportReasonEntity>

)