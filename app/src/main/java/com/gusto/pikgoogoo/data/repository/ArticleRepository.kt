package com.gusto.pikgoogoo.data.repository

import android.util.Log
import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.*
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticleRepository
@Inject
constructor(
    private val webService: WebService,
    private val articleMapper: ArticleMapper,
    private val voteHistoryMapper: VoteHistoryMapper,
    private val firebaseDataSource: FirebaseDataSource
) : ParentRepository() {

    private var articles = mutableListOf<Article>()

    fun insertArticleFlow(content: String, subjectId: Int, imageUrl: String, cropImage: Int) = flow {
        try {
            emit(DataState.Loading("항목 데이터 작업 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.addArticle(idToken, content, subjectId, imageUrl, cropImage)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(Pair(res.status.message, res.id)))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchArticlesFlow(subjectId: Int, order: Int, offset: Int, searchWords: String) = flow {
        try {
            emit(DataState.Loading("항목 정보 가져오는 중"))

            if (offset == 0) {
                articles.clear()
            }

            val resArticle = webService.getArticles(subjectId, order, offset, searchWords)
            if (isStatusCodeSuccess(resArticle)) {
                val data = articleMapper.mapFromEntityList(resArticle.articles)
                var totalVoteCount = 0
                if (data.isNotEmpty()) {
                    totalVoteCount = data[0].totalVotesInPeriod
                    for (item in data) {
                        if (item.imageUrl.isNotEmpty()) {
                            item.imageUri = firebaseDataSource.getThumbUri(item.imageUrl)
                        }
                        item.voteRate = if (totalVoteCount == 0) 0.0f else (item.voteCount.toFloat() / totalVoteCount.toFloat()) * 100.0f
                    }
                }
                articles.addAll(data)
                if (order != ArticleOrder.NEW.value) {
                    rankAll(articles)
                }
                emit(DataState.Success(articles))
            } else {
                emit(DataState.Error(formatErrorFromStatus(resArticle)))
            }

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun voteArticleFlow(articleId: Int, order: Int) = flow {
        try {
            emit(DataState.Loading("투표 정보 입력 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.voteArticle(idToken, articleId)
            if (isStatusCodeSuccess(res)) {
                val articleIndex = articles.indexOfFirst { it.id == articleId }
                if (articleIndex != -1) {
                    articles[articleIndex].apply {
                        voteCount += 1
                        totalVotesInPeriod += 1
                        voteRate = (voteCount.toFloat() / totalVotesInPeriod.toFloat()) * 100.0f
                    }
                }
                if (order != ArticleOrder.NEW.value) {
                    articles.sortByDescending { it.voteCount }
                } else {
                    rankAll(articles)
                }
                emit(DataState.Success(articles))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getVoteCountFlow(subjectId: Int, order: Int) = flow {
        try {
            emit(DataState.Loading("투표 수 가져오는 중"))
            val res = webService.getTotalVoteCount(subjectId, order)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.totalVoteCount))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getVoteHistoryFlow(articleId: Int, startDate: String, endDate: String) = flow {
        try {
            emit(DataState.Loading("투표 기록 가져오는 중"))
            val res = webService.getVoteHistory(articleId, startDate, endDate)
            if (isStatusCodeSuccess(res)) {
                val voteHistory = voteHistoryMapper.mapFromEntityList(res.voteHistoryData)
                emit(DataState.Success(voteHistory))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getArticleCreatedDateFlow(articleId: Int) = flow {
        try {
            emit(DataState.Loading("항목 생성 정보 가져오는 중"))
            val res = webService.getArticleCreatedDate(articleId)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.articleCreatedDate))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun rankAll(list: MutableList<Article>) {
        if (list.isEmpty()) {
            return
        }
        var rank = 1
        var realRank = 1
        var lastVoteCount = 0
        list.sortByDescending { it.voteCount }
        list.forEach { article ->
            if (!(article.voteCount == lastVoteCount && article.voteCount != 0)) {
                rank = realRank
            }
            article.rank = rank
            realRank++
            lastVoteCount = article.voteCount
        }
    }
    
}