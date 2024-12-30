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

}