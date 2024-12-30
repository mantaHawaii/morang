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
){

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

}