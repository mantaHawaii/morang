package com.gusto.pikgoogoo.data.repository

import android.util.Log
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

    fun updateLocalUserGradeFlow() = flow {
        try {
            emit(DataState.Loading("로컬 DB 업데이트 중"))
            Log.d("MR_GR", "updateLocalUserGradeFlow()")
            val res = webService.getGrade()
            Log.d("MR_GR", "updateLocalUserGradeFlow() 2")
            if (isStatusCodeSuccess(res)) {
                Log.d("MR_GR", "updateLocalUserGradeFlow() 3")
                val gradeList = gradeMapper.mapFromEntityList(res.grade)
                Log.d("MR_GR", "updateLocalUserGradeFlow() 4")
                for (grade in gradeList) {
                    Log.d("MR_GR", "updateLocalUserGradeFlow() 5")
                    gradeDao.insertGradeAll(grade)
                    Log.d("MR_GR", "updateLocalUserGradeFlow() 6")
                }
                emit(DataState.Success(res.status.message))
            } else {
                Log.d("MR_GR", "updateLocalUserGradeFlow() 7")
                emit(DataState.Error(formatErrorFromStatus(res)))
                Log.d("MR_GR", "updateLocalUserGradeFlow() 8")
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

    @Deprecated("2025-01-15 이후로 사용하지 않는 함수")
    suspend fun getGradeFromLocal(): List<Grade> {
        return withContext(Dispatchers.Default) {
            gradeDao.getGradeAll()
        }
    }

}