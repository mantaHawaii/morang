package com.gusto.pikgoogoo.data.repository

import android.content.Context
import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.Comment
import com.gusto.pikgoogoo.data.CommentMapper
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.datasource.PreferenceDataSource
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentRepository
@Inject
constructor(
    private val webService: WebService,
    private val commentMapper: CommentMapper,
    private val firebaseDataSource: FirebaseDataSource,
    private val preferenceDataSource: PreferenceDataSource
) : ParentRepository() {

    private val comments = mutableListOf<Comment>()

    fun postCommentToArticleFlow(articleId: Int, subjectId: Int, comment: String) = flow {
        try {
            emit(DataState.Loading("코멘트 입력 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.commentOnArticle(idToken, articleId, subjectId, comment)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.status.message))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    fun fetchCommentsFlow(subjectId: Int, articleId: Int, order: Int, offset: Int) = flow {
        try {
            emit(DataState.Loading("코멘트 가져오는 중"))
            val res = webService.getComments(subjectId, articleId, order, offset)
            if (isStatusCodeSuccess(res)) {
                if (offset == 0) {
                    comments.clear()
                }
                val commentList = commentMapper.mapFromEntityList(res.comments)
                comments.addAll(commentList)
                emit(DataState.Success(comments))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun likeCommentFlow(commentId: Int) = flow {
        try {
            emit(DataState.Loading("좋아요 요청 처리 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.likeComment(idToken, commentId)
            if (isStatusCodeSuccess(res)) {
                val commentIndex = comments.indexOfFirst { it.id == commentId }
                if (commentIndex != -1) {
                    comments[commentIndex].likeCount += 1
                }
                emit(DataState.Success(comments))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun updateCommentFlow(commentId: Int, comment: String) = flow {
        try {
            emit(DataState.Loading("좋아요 요청 처리 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.editComment(idToken, commentId, comment)
            if (isStatusCodeSuccess(res)) {
                val commentIndex = comments.indexOfFirst { it.id == commentId }
                if (commentIndex != -1) {
                    comments[commentIndex].comment = comment
                }
                emit(DataState.Success(comments))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun removeCommentFlow(commentId: Int) = flow {
        try {
            emit(DataState.Loading("코멘트 삭제 요청 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.deleteComment(idToken, commentId)
            if (isStatusCodeSuccess(res)) {
                val commentIndex = comments.indexOfFirst { it.id == commentId }
                if (commentIndex != -1) {
                    comments.removeAt(commentIndex)
                }
                emit(DataState.Success(comments))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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

    fun fetchMyComments(context: Context, order: Int, offset: Int) = flow {
        try {
            emit(DataState.Loading("코멘트 가져오는 중"))
            val uid = preferenceDataSource.getUid(context)
            val res = webService.getUserComments(uid, order, offset)
            if (isStatusCodeSuccess(res)) {
                if (offset == 0) {
                    comments.clear()
                }
                val data = commentMapper.mapFromEntityList(res.comments)
                comments.addAll(data)
                emit(DataState.Success(comments))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}