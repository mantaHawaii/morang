package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.Response
import com.gusto.pikgoogoo.data.Subject
import com.gusto.pikgoogoo.data.SubjectMapper
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepository
@Inject
constructor(
    val webService: WebService,
    val subjectMapper: SubjectMapper
){

    suspend fun addSubjectFlow(title: String, category: Int, token: String): Flow<DataState<Pair<String, Int>>> = flow {
        emit(DataState.Loading())
        try {
            val res = webService.addSubject(token, title, category)
            if (res.status.code.equals("111")) {
                val pair = Pair(res.status.message, res.id)
                emit(DataState.Success(pair))
            } else {
                emit(DataState.Failure(res.status.message))
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addSubject(title: String, category: Int, token: String): Pair<String, Int> {
        val res = withContext(Dispatchers.IO) {
            webService.addSubject(token, title, category)
        }
        if (res.status.code == "111") {
            return Pair(res.status.message, res.id)
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getSubjectsFlow(categoryId: Int, order: Int, offset: Int): Flow<DataState<List<Subject>>> = flow {
        emit(DataState.Loading())
        try {
            val res = webService.getSubjects(categoryId, order, offset)
            val status = res.status
            if (status.code.equals("111")) {
                val subjects = subjectMapper.mapFromEntityList(res.subjects)
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Failure(status.message))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getSubjects(categoryId: Int, order: Int, offset: Int): List<Subject> {
        val res = withContext(Dispatchers.IO) {
            webService.getSubjects(categoryId, order, offset)
        }
        if (res.status.code == "111") {
            return subjectMapper.mapFromEntityList(res.subjects)
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getSubjectsBySearchWordsFlow(categoryId: Int, order: Int, offset: Int, searchWords: String): Flow<DataState<List<Subject>>> = flow {
        emit(DataState.Loading())
        try {
            val res = webService.getSubjectsBySearchWords(categoryId, order, offset, searchWords)
            val status = res.status
            if (status.code.equals("111")) {
                val subjects = subjectMapper.mapFromEntityList(res.subjects)
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Failure(status.message))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getSubjectsBySearchWords(categoryId: Int, order: Int, offset: Int, searchWords: String): List<Subject> {
        val res = withContext(Dispatchers.IO) {
            webService.getSubjectsBySearchWords(categoryId, order, offset, searchWords)
        }
        if (res.status.code == "111") {
            return subjectMapper.mapFromEntityList(res.subjects)
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun isBookmarkedFlow(subjectId: Int, userId: Int): Flow<Boolean> = flow {
        emit(false)
        try {
            val res = webService.isBookmarked(subjectId, userId)
            if (res.status.code.equals("111")) {
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun isBookmarked(subjectId: Int, userId: Int): Boolean {
        val res = withContext(Dispatchers.IO) {
                webService.isBookmarked(subjectId, userId)
        }
        if (res.status.code == "111") {
            return true
        } else {
            return false
        }
    }

    suspend fun bookmarkSubjectFlow(subjectId: Int, token: String): Flow<Response> = flow {
        val res = webService.bookmarkSubject(subjectId, token)
        emit(res)
    }.flowOn(Dispatchers.IO)

    suspend fun bookmarkSubject(subjectId: Int, token: String): String {
        val res = withContext(Dispatchers.IO) {
            webService.bookmarkSubject(subjectId, token)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getBookmarkedSubjectsFlow(userId: Int, order: Int, offset: Int): Flow<DataState<List<Subject>>> = flow {
        emit(DataState.Loading())
        try {
            val res = webService.getBookmarkedSubjects(userId, order, offset)
            if (res.status.code == "111") {
                val subjects = subjectMapper.mapFromEntityList(res.subjects)
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Failure(res.status.message))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getBookmarkedSubjects(userId: Int, order: Int, offset: Int): List<Subject> {
        val res = withContext(Dispatchers.IO) {
            webService.getBookmarkedSubjects(userId, order, offset)
        }
        if (res.status.code == "111") {
            return subjectMapper.mapFromEntityList(res.subjects)
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getMySubjectsFlow(userId: Int, order: Int, offset: Int): Flow<DataState<List<Subject>>> = flow {
        emit(DataState.Loading())
        try {
            val res = webService.getMySubjects(userId, order, offset)
            if (res.status.code == "111") {
                val subjects = subjectMapper.mapFromEntityList(res.subjects)
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Failure(res.status.message))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getMySubjects(userId: Int, order: Int, offset: Int): List<Subject> {
        val res = withContext(Dispatchers.IO) {
            webService.getMySubjects(userId, order, offset)
        }
        if (res.status.code == "111") {
            return subjectMapper.mapFromEntityList(res.subjects)
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getTitle(subjectId: Int): Flow<DataState<String>> = flow {
        emit(DataState.Loading())
        try {
            val res = webService.getTitle(subjectId)
            if (res.status.code == "111") {
                emit(DataState.Success(res.title))
            } else {
                emit(DataState.Failure(res.status.message))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}