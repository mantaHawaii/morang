package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.GradeMapper
import com.gusto.pikgoogoo.data.dao.GradeDao
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GradeRepository
@Inject
constructor(
    private val webService: WebService,
    private val gradeDao: GradeDao,
    private val gradeMapper: GradeMapper
){

    suspend fun getServerDBVersion(): Int {
        val res = withContext(Dispatchers.IO) {
            webService.getServerDBVersion()
        }
        if (res.status.code =="111") {
            return res.dbVersion
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun insertGradeFromServer(): String {
        val res = withContext(Dispatchers.IO) {
            webService.getGrade()
        }
        if (res.status.code == "111") {
            val gradeList = gradeMapper.mapFromEntityList(res.grade)
            for (grade in gradeList) {
                gradeDao.insertGradeAll(grade)
            }
            return "등급 정보 업데이트가 완료되었습니다"
        } else {
            throw Exception(res.status.message)
        }
    }

    suspend fun getGradeFromLocalFlow(): Flow<DataState<List<Grade>>> = flow {
        emit(DataState.Loading())
        try {
            emit(DataState.Success(gradeDao.getGradeAll()))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getGradeFromLocal(): List<Grade> {
        return withContext(Dispatchers.Default) {
            gradeDao.getGradeAll()
        }
    }

    suspend fun setGradeIcon(token: String, gradeIcon: Int): Int {
        val res = withContext(Dispatchers.IO) {
                webService.editUserGradeIcon(token, gradeIcon)
        }
        if (res.status.code == "111") {
            return res.id
        } else {
            throw Exception(res.status.message)
        }
    }

}