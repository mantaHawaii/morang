package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.*
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticleRepository
@Inject
constructor(
    private val webService: WebService,
    private val articleMapper: ArticleMapper,
    private val voteHistoryMapper: VoteHistoryMapper
){

    suspend fun insertArticle(token: String, content: String, subjectId: Int, imageUrl: String, cropImage: Int): String {
        val res = withContext(Dispatchers.IO) {
            webService.addArticle(token, content, subjectId, imageUrl, cropImage)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun fetchArticles(subjectId: Int, order: Int, offset: Int, searchWords: String): List<Article> {
        val res = withContext(Dispatchers.IO) {
            webService.getArticles(subjectId, order, offset, searchWords)
        }
        if (res.status.code == "111") {
            return articleMapper.mapFromEntityList(res.articles)
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun voteArticle(token: String, articleId: Int): String {
        val res = withContext(Dispatchers.IO) {
            webService.voteArticle(token, articleId)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getVoteCount(subjectId: Int, order: Int): Int {
        val res = withContext(Dispatchers.IO) {
            webService.getTotalVoteCount(subjectId, order)
        }
        if (res.status.code == "111") {
            return res.totalVoteCount
        } else {
            throw Exception(res.status.code+":"+res.status.message)
        }
    }

    suspend fun getVoteHistory(articleId: Int, startDate: String, endDate: String): List<VoteHistory> {
        val res = withContext(Dispatchers.IO) {
            webService.getVoteHistory(articleId, startDate, endDate)
        }
        if (res.status.code == "111") {
            return voteHistoryMapper.mapFromEntityList(res.voteHistoryData)
        } else {
            throw Exception(res.status.code+":"+res.status.message)
        }
    }

    suspend fun getArticleCreatedDate(articleId: Int): String {
        val res = withContext(Dispatchers.IO) {
            webService.getArticleCreatedDate(articleId)
        }
        if (res.status.code == "111") {
            return res.articleCreatedDate
        } else {
            throw Exception(res.status.code+":"+res.status.message)
        }
    }
    
}