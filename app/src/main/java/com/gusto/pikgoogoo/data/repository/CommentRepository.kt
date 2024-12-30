package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.Comment
import com.gusto.pikgoogoo.data.CommentMapper
import com.gusto.pikgoogoo.data.Response
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentRepository
@Inject
constructor(
    private val webService: WebService,
    private val commentMapper: CommentMapper
){
    suspend fun commentOnArticle(token: String, articleId: Int, subjectId: Int, comment: String): String {
        val res = withContext(Dispatchers.IO) {
            webService.commentOnArticle(token, articleId, subjectId, comment)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getComments(subjectId: Int, articleId: Int, order: Int, offset: Int): List<Comment> {
        val res = withContext(Dispatchers.IO) {
            webService.getComments(subjectId, articleId, order, offset)
        }
        if (res.status.code == "111") {
            return commentMapper.mapFromEntityList(res.comments)
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun likeComment(token: String, commentId: Int): String {
        val res = withContext(Dispatchers.IO) {
            webService.likeComment(token, commentId)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun editComment(token: String, commentId: Int, comment: String): String {
        val res = withContext(Dispatchers.IO) {
            webService.editComment(token, commentId, comment)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun deleteComment(token: String, commentId: Int): String {
        val res = withContext(Dispatchers.IO) {
            webService.deleteComment(token, commentId)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getUserComments(userId: Int, order: Int, offset: Int): List<Comment> {
        val res = withContext(Dispatchers.IO) {
            webService.getUserComments(userId, order, offset)
        }
        if (res.status.code == "111") {
            return commentMapper.mapFromEntityList(res.comments)
        } else {
            throw Exception(res.status.message)
        }
    }

}