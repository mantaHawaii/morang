package com.gusto.pikgoogoo.api

import com.gusto.pikgoogoo.data.Response
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WebService {

    @POST("get_server_db_version.php")
    suspend fun getServerDBVersion(): Response

    @FormUrlEncoded
    @POST("get_user_by_token.php")
    suspend fun getUserByToken(
        @Field("token") token: String
    ): Response

    @FormUrlEncoded
    @POST("get_user_by_id.php")
    suspend fun getUserById(
        @Field("userId") id: Int
    ): Response

    @FormUrlEncoded
    @POST("register_user.php")
    suspend fun registerUser(
        @Field("token") token: String,
        @Field("nickname") nickname: String
    ): Response

    @FormUrlEncoded
    @POST("edit_user.php")
    suspend fun editUser(
        @Field("token") token: String,
        @Field("nickname") nickname: String
    ): Response

    @FormUrlEncoded
    @POST("edit_grade_icon.php")
    suspend fun editUserGradeIcon(
        @Field("token") token: String,
        @Field("gradeIcon") gradeIcon: Int
    ): Response

    @POST("get_categories.php")
    suspend fun getCategories(): Response

    @FormUrlEncoded
    @POST("insert_subject.php")
    suspend fun addSubject(
        @Field("token") token: String,
        @Field("title") title: String,
        @Field("categoryId") categoryId: Int
    ): Response

    @FormUrlEncoded
    @POST("get_subjects.php")
    suspend fun getSubjects(
        @Field("categoryId") categoryId: Int,
        @Field("order") order: Int,
        @Field("offset") offset: Int
    ): Response

    @FormUrlEncoded
    @POST("get_title.php")
    suspend fun getTitle(
        @Field("subjectId") subjectId: Int
    ): Response

    @FormUrlEncoded
    @POST("get_subjects_by_searchwords.php")
    suspend fun getSubjectsBySearchWords(
        @Field("categoryId") categoryId: Int,
        @Field("order") order: Int,
        @Field("offset") offset: Int,
        @Field("searchWords") searchWords: String
    ): Response

    @FormUrlEncoded
    @POST("get_bookmarked_subjects.php")
    suspend fun getBookmarkedSubjects(
        @Field("userId") userId: Int,
        @Field("order") order: Int,
        @Field("offset") offset: Int
    ): Response

    @FormUrlEncoded
    @POST("get_my_subjects.php")
    suspend fun getMySubjects(
        @Field("userId") userId: Int,
        @Field("order") order: Int,
        @Field("offset") offset: Int
    ): Response

    @FormUrlEncoded
    @POST("insert_article.php")
    suspend fun addArticle(
        @Field("token") token: String,
        @Field("content") content: String,
        @Field("subjectId") subjectId: Int,
        @Field("imageUrl") imageUrl: String,
        @Field("cropImage") cropImage: Int
    ): Response

    @FormUrlEncoded
    @POST("get_articles.php")
    suspend fun getArticles(
        @Field("subjectId") subjectId: Int,
        @Field("order") order: Int,
        @Field("offset") offset: Int,
        @Field("searchWords") searchWords: String
    ): Response

    @FormUrlEncoded
    @POST("get_article_date.php")
    suspend fun getArticleCreatedDate(
        @Field("articleId") articleId: Int
    ): Response

    @FormUrlEncoded
    @POST("vote_article.php")
    suspend fun voteArticle(
        @Field("token") token: String,
        @Field("articleId") articleId: Int
    ): Response

    @FormUrlEncoded
    @POST("get_total_vote_count.php")
    suspend fun getTotalVoteCount(
        @Field("subjectId") subjectId: Int,
        @Field("order") order: Int
    ): Response

    @FormUrlEncoded
    @POST("get_vote_history.php")
    suspend fun getVoteHistory(
        @Field("articleId") articleId: Int,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String
    ): Response

    @FormUrlEncoded
    @POST("comment_on_article.php")
    suspend fun commentOnArticle(
        @Field("token") token: String,
        @Field("articleId") articleId: Int,
        @Field("subjectId") subjectId: Int,
        @Field("comment") comment: String
    ): Response

    @FormUrlEncoded
    @POST("get_comments.php")
    suspend fun getComments(
        @Field("subjectId") subjectId: Int,
        @Field("articleId") articleId: Int,
        @Field("order") order: Int,
        @Field("offset") offset: Int
    ): Response

    @FormUrlEncoded
    @POST("get_user_comments.php")
    suspend fun getUserComments(
        @Field("userId") userId: Int,
        @Field("order") order: Int,
        @Field("offset") offset: Int
    ): Response

    @FormUrlEncoded
    @POST("like_comment.php")
    suspend fun likeComment(
        @Field("token") token: String,
        @Field("commentId") commentId: Int
    ): Response

    @FormUrlEncoded
    @POST("edit_comment.php")
    suspend fun editComment(
        @Field("token") token: String,
        @Field("commentId") commentId: Int,
        @Field("comment") comment: String
    ): Response

    @FormUrlEncoded
    @POST("delete_comment.php")
    suspend fun deleteComment(
        @Field("token") token: String,
        @Field("commentId") commentId: Int
    ): Response

    @FormUrlEncoded
    @POST("get_report_reasons.php")
    suspend fun getReportReasons(
        @Field("type") type: Int
    ): Response

    @FormUrlEncoded
    @POST("report_article.php")
    suspend fun reportArticle(
        @Field("articleId") articleId: Int,
        @Field("reportId") reportId: Int,
        @Field("userId") userId: Int
    ): Response

    @FormUrlEncoded
    @POST("report_comment.php")
    suspend fun reportComment(
        @Field("commentId") commentId: Int,
        @Field("reportId") reportId: Int,
        @Field("userId") userId: Int
    ): Response

    @FormUrlEncoded
    @POST("bookmark_subject.php")
    suspend fun bookmarkSubject(
        @Field("subjectId") subjectId: Int,
        @Field("token") token: String
    ): Response

    @FormUrlEncoded
    @POST("is_bookmarked.php")
    suspend fun isBookmarked(
        @Field("subjectId") subjectId: Int,
        @Field("userId") userId: Int
    ): Response

    @POST("get_grade.php")
    suspend fun getGrade(): Response

    @FormUrlEncoded
    @POST("delete_user.php")
    suspend fun deleteUser(
        @Field("token") token: String
    ): Response

}