package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.GradeMapper
import com.gusto.pikgoogoo.data.dao.GradeDao
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GradeRepository
@Inject
constructor(
    private val webService: WebService,
    private val gradeDao: GradeDao,
    private val gradeMapper: GradeMapper,
    private val firebaseDataSource: FirebaseDataSource
): ParentRepository() {

    fun getServerDBVersionFlow() = flow {
        try {
            emit(DataState.Loading("DB 버전 가져오는 중"))
            val res = webService.getServerDBVersion()
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.dbVersion))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun updateLocalUserGrade() = flow {
        try {
            emit(DataState.Loading("로컬 DB 업데이트 중"))
            val res = webService.getGrade()
            if (isStatusCodeSuccess(res)) {
                val gradeList = gradeMapper.mapFromEntityList(res.grade)
                for (grade in gradeList) {
                    gradeDao.insertGradeAll(grade)
                }
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getGradeFromLocalFlow(): Flow<DataState<List<Grade>>> = flow {
        try {
            emit(DataState.Loading("등급 정보 가져오는 중"))
            emit(DataState.Success(gradeDao.getGradeAll()))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun upgradeGradeIconFlow(gradeIcon: Int) = flow {
        try {
            emit(DataState.Loading("등급 정보 업데이트 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.editUserGradeIcon(idToken, gradeIcon)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.id))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    //삭제 완료
    @Deprecated("2025-01-15이후로 사용하지 않는 함수")
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

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용하지 않는 함수")
    suspend fun insertGradeFromServer(): String {
        val res = withContext(Dispatchers.IO) {
            webService.getGrade()
        }
        if (res.status.code == "111") {
            val gradeList = gradeMapper.mapFromEntityList(res.grade)
            withContext(Dispatchers.Default) {
                for (grade in gradeList) {
                    gradeDao.insertGradeAll(grade)
                }
            }
            return "등급 정보 업데이트가 완료되었습니다"
        } else {
            throw Exception(res.status.message)
        }
    }

    @Deprecated("2025-01-15 이후로 사용하지 않는 함수")
    suspend fun getGradeFromLocal(): List<Grade> {
        return withContext(Dispatchers.Default) {
            gradeDao.getGradeAll()
        }
    }

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용하지 않는 함수")
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