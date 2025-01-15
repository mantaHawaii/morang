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

    //삭제 완료
    @Deprecated("2025-01-15 이후로 삭제된 함수")
    suspend fun storeImage(uri: Uri, fileName: String): String {
        val fileForder = "articles/"
        val fileExtension = ".jpg"

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child(fileForder + fileName + fileExtension)
        val result = imageRef.putFile(uri).await()
        val metadata = result.metadata?:throw Exception("메타데이터를 가져올 수 없습니다")
        val path = modifyString(metadata.path)

        return path
    }

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

    suspend fun getThumbUri(imageUrl: String): Uri {
        val resizedSize = "_400x400"
        val fileExtension = ".jpg"
        val storageRef = Firebase.storage.reference
        val thumbRef = storageRef.child(imageUrl+resizedSize+fileExtension)
        val uri = thumbRef.downloadUrl.await()
        if (uri != null) {
            return uri
        } else {
            throw Exception("썸네일 불러오기 실패")
        }
    }

    suspend fun getThumbUriFlow(imageUrl: String) = flow {
        emit(DataState.Loading("썸네일 가져오는 중"))
        try {
            val thumbnailUri = firebaseDataSource.getThumbUri(imageUrl)
            emit(DataState.Success(thumbnailUri))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun modifyString(input: String): String {
        val index = input.indexOf('/')
        return if (index != -1) {
            input.substring(0, index + 1) + "thumbnails/" + input.substring(index + 1)
        } else {
            input
        }
    }

    private fun generateFileName(uid: Int): String {
        if (uid > 0) {
            return uid.toString() + "_" + System.currentTimeMillis().toString()
        } else {
            throw Exception("잘못된 형식의 UID입니다")
        }
    }


}