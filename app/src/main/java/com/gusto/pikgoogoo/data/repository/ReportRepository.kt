package com.gusto.pikgoogoo.data.repository

import android.content.Context
import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.ReportReason
import com.gusto.pikgoogoo.data.ReportReasonMapper
import com.gusto.pikgoogoo.datasource.PreferenceDataSource
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
    private val rrMapper: ReportReasonMapper,
    private val preferenceDataSource: PreferenceDataSource
) : ParentRepository() {

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

    fun reportArticleFlow(context: Context, articleId: Int, reportId: Int) = flow {
        try {
            emit(DataState.Loading())
            val userId = preferenceDataSource.getUid(context)
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

    fun reportCommentFlow(context: Context, commentId: Int, reportId: Int) = flow {
        try {
            emit(DataState.Loading())
            val userId = preferenceDataSource.getUid(context)
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