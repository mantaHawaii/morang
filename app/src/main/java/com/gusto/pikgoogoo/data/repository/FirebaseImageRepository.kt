package com.gusto.pikgoogoo.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.datasource.PreferenceDataSource
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseImageRepository
@Inject
constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val preferenceDataSource: PreferenceDataSource
) : ParentRepository() {

    fun storeImageFlow(uri: Uri, context: Context) = flow {
        try {
            emit(DataState.Loading("이미지 저장 중"))
            val uid = preferenceDataSource.getUid(context)
            val fileName = generateFileName(uid)
            emit(DataState.Success(firebaseDataSource.storeImage(uri, fileName)))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getThumbUriFlow(imageUrl: String) = flow {
        emit(DataState.Loading("썸네일 가져오는 중"))
        try {
            val thumbnailUri = firebaseDataSource.getThumbUri(imageUrl)
            emit(DataState.Success(thumbnailUri))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun generateFileName(uid: Int): String {
        if (uid > 0) {
            return uid.toString() + "_" + System.currentTimeMillis().toString()
        } else {
            throw Exception("잘못된 형식의 UID입니다")
        }
    }

}