package com.gusto.pikgoogoo.data.repository

import android.content.Context
import com.google.firebase.functions.FirebaseFunctions
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.User
import com.gusto.pikgoogoo.data.UserMapper
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.datasource.GoogleDataSource
import com.gusto.pikgoogoo.datasource.KakaoDataSource
import com.gusto.pikgoogoo.datasource.PreferenceDataSource
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository
@Inject
constructor(
    private val webService: WebService,
    private val userMapper: UserMapper,
    private val firebaseDataSource: FirebaseDataSource,
    private val kakaoDataSource: KakaoDataSource,
    private val googleDataSource: GoogleDataSource,
    private val preferenceDataSource: PreferenceDataSource
): ParentRepository() {

    //삭제 완료
    @Deprecated("2025-01-15부터 사용하지 않는 함수")
    suspend fun getUserByToken(token: String): User {
        val res = withContext(Dispatchers.IO) {
            webService.getUserByToken(token)
        }
        if (res.status.code == "111") {
            return userMapper.mapFromEntity(res.user)
        } else if (res.status.code == "001") {
            //유저 등록이 안된 경우
            return User(id = -1, nickname = "", grade = 0, gradeIcon = 0, isRestricted = 0)
        } else {
            throw Exception(res.status.message)
        }
    }

    //
    @Deprecated("2025-01-15부터 사용하지 않는 함수")
    suspend fun getUserById(id: Int): User {
        val res = withContext(Dispatchers.IO) {
            webService.getUserById(id)
        }
        if (res.status.code == "111") {
            return userMapper.mapFromEntity(res.user)
        } else {
            throw Exception(res.status.message)
        }
    }

    @Deprecated("2025/01/15부터 사용되지 않는 함수")
    suspend fun registerUser(token: String, nickname: String): String {
        val res = withContext(Dispatchers.IO) {
            webService.registerUser(token, nickname)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    fun registerUserFlow(nickname: String) = flow {
        try {
            emit(DataState.Loading("유저 등록 시작"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.registerUser(idToken, nickname)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Loading(res.status.message))
                emit(DataState.Success(res.id))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    @Deprecated("2025-01-15부터 사용하지 않는 함수")
    suspend fun editUser(token: String, nickname: String): String {
        val res = withContext(Dispatchers.IO) {
            webService.editUser(token, nickname)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    fun editUserFlow(nickname: String) = flow {
        try {
            emit(DataState.Loading("닉네임 변경 요청 보내는 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.editUser(idToken, nickname)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.status.message))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    @Deprecated("2025-01-15부터 사용하지 않는 함수")
    suspend fun requestWithdrawal(): String {
        val functionResult = FirebaseFunctions.getInstance("asia-northeast3")
            .getHttpsCallable("deleteUser")
            .call()
            .await()
        functionResult.getData() ?: throw Exception("실패")
        return functionResult.getData().toString()
    }

    @Deprecated("2025-01-15부터 사용하지 않는 함수")
    suspend fun deleteUserFromServer(token: String): String {
        val res = withContext(Dispatchers.IO) {
            webService.deleteUser(token)
        }
        if (res.status.code == "111") {
            return res.status.message
        } else {
            throw Exception(res.status.message)
        }
    }

    fun deleteUserFlow() = flow {
        try {
            emit(DataState.Loading("유저 삭제 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.deleteUser(idToken)
            if (isStatusCodeSuccess(res)) {
                if (firebaseDataSource.deleteUser(firebaseUser)) {
                    emit(DataState.Success(res.status.message))
                } else {
                    emit(DataState.Error(Exception("유저 삭제 실패")))
                }
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getUidFromShareRef(context: Context): Int {
        try {
            val uid = preferenceDataSource.getUid(context)
            return uid
        } catch (e: Exception) {
            return -1
        }
    }

    fun getCurrentMorangUserFlow(context: Context) = flow {
        try {
            emit(DataState.Loading("유저 정보 가져오는 중"))
            val uid = preferenceDataSource.getUid(context)
            val res = webService.getUserById(uid)
            if (isStatusCodeSuccess(res)) {
                val user = userMapper.mapFromEntity(res.user)
                emit(DataState.Success(user))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getMorangUserWithGoogleFlow(context: Context) = flow {
        try {
            emit(DataState.Loading("자격 증명 정보 가져오는 중"))
            val credential = googleDataSource.getCredential(context)

            emit(DataState.Loading("구글 ID토큰 정보 가져오는 중"))
            val idTokenCredential = googleDataSource.getGoogleIdTokenCredential(credential)

            emit(DataState.Loading("파이어베이스 자격 증명 정보 가져오는 중"))
            val authCredential = firebaseDataSource.getAuthCredential(idTokenCredential)

            emit(DataState.Loading("파이어베이스 유저 정보 가져오는 중"))
            val user = firebaseDataSource.signWithAuthCredential(authCredential)

            emit(DataState.Loading("파이어베이스 ID 토큰 정보 가져오는 중"))
            val idToken = firebaseDataSource.getIDTokenByUser(user)

            emit(DataState.Loading("모랭 서버에서 유저 정보 가져오는 중"))
            val res = webService.getUserByToken(idToken)

            if (res.status.code == "111") {
                val morangUser = userMapper.mapFromEntity(res.user)
                emit(DataState.Success(morangUser))
            } else if (res.status.code == "001") {
                val emptyUser = User(id = -1, nickname = "", grade = 0, gradeIcon = 0, isRestricted = 0)
                emit(DataState.Success(emptyUser))
            } else {
                emit(DataState.Error(Exception("접속이 실패하였습니다")))
            }

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getMorangUserWithKakaoFlow(context: Context) = flow {
        try {
            emit(DataState.Loading("카카오 토큰 가져오는 중"))
            val kakaoToken = kakaoDataSource.getKakaoToken(context)

            emit(DataState.Loading("파이어베이스 유저 정보 가져오는 중"))
            val user = firebaseDataSource.signInWithKakaoToken(kakaoToken)

            emit(DataState.Loading("파이어베이스 ID 토큰 가져오는 중"))
            val idToken = firebaseDataSource.getIDTokenByUser(user)

            emit(DataState.Loading("모랭 서버에서 유저 정보 가져오는 중"))
            val res = webService.getUserByToken(idToken)

            if (res.status.code == "111") {
                val morangUser = userMapper.mapFromEntity(res.user)
                emit(DataState.Success(morangUser))
            } else if (res.status.code == "001") {
                val emptyUser = User(id = -1, nickname = "", grade = 0, gradeIcon = 0, isRestricted = 0)
                emit(DataState.Success(emptyUser))
            } else {
                emit(DataState.Error(Exception("접속이 실패하였습니다")))
            }

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getMorangUserWithGPFlow(uid: String) = flow {
        try {
            emit(DataState.Loading("파이어베이스 유저 정보 가져오는 중"))
            val user = firebaseDataSource.signInWithUid(uid)

            emit(DataState.Loading("파이어베이스 ID 토큰 정보 가져오는 중"))
            val idToken = firebaseDataSource.getIDTokenByUser(user)

            emit(DataState.Loading("모랭 서버에서 유저 정보 가져오는 중"))
            val res = webService.getUserByToken(idToken)

            if (res.status.code == "111") {
                val morangUser = userMapper.mapFromEntity(res.user)
                emit(DataState.Success(morangUser))
            } else if (res.status.code == "001") {
                val emptyUser = User(id = -1, nickname = "", grade = 0, gradeIcon = 0, isRestricted = 0)
                emit(DataState.Success(emptyUser))
            } else {
                emit(DataState.Error(Exception("접속이 실패하였습니다")))
            }

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}