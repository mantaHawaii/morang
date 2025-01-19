package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.ReportReason
import com.gusto.pikgoogoo.data.ReportReasonMapper
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportRepository
@Inject
constructor(
    private val webService: WebService,
    private val rrMapper: ReportReasonMapper
) : ParentRepository() {

    suspend fun getReportReasons(type: Int): List<ReportReason> {
        val res = withContext(Dispatchers.IO) {
            webService.getReportReasons(type)
        }
        if (res.status.code == "111") {
            return rrMapper.mapFromEntityList(res.reportReasons)
        } else {
            throw Exception(res.status.message)
        }
    }

    fun fetchReportReasonsFlow(type: Int) = flow {
        try {
            emit(DataState.Loading())
            val res = webService.getReportReasons(type)
            if (isStatusCodeSuccess(res)) {
                val data = rrMapper.mapFromEntityList(res.reportReasons)
                emit(DataState.Success(data))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun reportArticle(articleId: Int, reportId: Int, userId: Int): String {
        val res = withContext(Dispatchers.IO) {
            webService.reportArticle(articleId, reportId, userId)
        }
        if (res.status.code.equals("111")) {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    fun reportArticleFlow(articleId: Int, reportId: Int, userId: Int) = flow {
        try {
            emit(DataState.Loading())
            val res = webService.reportArticle(articleId, reportId, userId)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.status.message))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun reportComment(commentId: Int, reportId: Int, userId: Int): String {
        val res = withContext(Dispatchers.IO) {
            webService.reportComment(commentId, reportId, userId)
        }
        if (res.status.code.equals("111")) {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    fun reportCommentFlow(commentId: Int, reportId: Int, userId: Int) = flow {
        try {
            emit(DataState.Loading())
            val res = webService.reportComment(commentId, reportId, userId)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.status.message))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}