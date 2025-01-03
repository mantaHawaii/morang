package com.gusto.pikgoogoo.data.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseImageRepository
@Inject
constructor() {

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

    private fun modifyString(input: String): String {
        val index = input.indexOf('/')
        return if (index != -1) {
            input.substring(0, index + 1) + "thumbnails/" + input.substring(index + 1)
        } else {
            input
        }
    }


}