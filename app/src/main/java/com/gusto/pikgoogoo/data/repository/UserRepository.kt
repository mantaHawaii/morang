package com.gusto.pikgoogoo.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.User
import com.gusto.pikgoogoo.data.UserMapper
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    private val auth: FirebaseAuth
){

    suspend fun getUserByToken(token: String): User {
        val res = withContext(Dispatchers.IO) {
            webService.getUserByToken(token)
        }
        if (res.status.code == "111") {
            return userMapper.mapFromEntity(res.user)
        } else {
            throw Exception(res.status.message)
        }
    }

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

    suspend fun requestWithdrawal(): String {
        val functionResult = FirebaseFunctions.getInstance("asia-northeast3")
            .getHttpsCallable("deleteUser")
            .call()
            .await()
        functionResult.data ?: throw Exception("실패")
        return functionResult.data.toString()
    }

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

    fun getUidFromShareRef(context: Context): Int {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val uid = sharedPref.getInt(context.getString(R.string.preference_user_key), 0)
        if (uid > 0) {
            return uid
        } else {
            throw Exception("저장된 UID 정보가 없습니다")
        }
    }

}