package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.*
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
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
    private val voteHistoryMapper: VoteHistoryMapper,
    private val firebaseDataSource: FirebaseDataSource
) : ParentRepository() {

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용하지 않는 함수입니다")
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

    fun insertArticleFlow(content: String, subjectId: Int, imageUrl: String, cropImage: Int) = flow {
        try {
            emit(DataState.Loading("항목 데이터 작업 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val result = insertArticle(idToken, content, subjectId, imageUrl, cropImage)
            emit(DataState.Success(result))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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

    fun fetchArticlesFlow(subjectId: Int, order: Int, offset: Int, searchWords: String) = flow {
        emit(DataState.Loading("항목 정보 가져오는 중"))
        try {
            val result = fetchArticles(subjectId, order, offset, searchWords)
            emit(DataState.Success(result))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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

    fun voteArticleFlow(token: String, articleId: Int) = flow {
        try {
            emit(DataState.Loading("투표 정보 입력 중"))
            val result = voteArticle(token, articleId)
            emit(DataState.Success(result))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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

    fun getVoteCountFlow(subjectId: Int, order: Int) = flow {
        try {
            emit(DataState.Loading("투표 수 가져오는 중"))
            val result = getVoteCount(subjectId, order)
            emit(DataState.Success(result))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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

    fun getVoteHistoryFlow(articleId: Int, startDate: String, endDate: String) = flow {
        try {
            emit(DataState.Loading("투표 기록 가져오는 중"))
            val result = getVoteHistory(articleId, startDate, endDate)
            emit(DataState.Success(result))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

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

    fun getArticleCreatedDateFlow(articleId: Int) = flow {
        try {
            emit(DataState.Loading("항목 생성 정보 가져오는 중"))
            val result = getArticleCreatedDate(articleId)
            emit(DataState.Success(result))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)
    
}