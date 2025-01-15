package com.gusto.pikgoogoo.datasource

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.auth.model.OAuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseDataSourceImpl: FirebaseDataSource {

    override suspend fun storeImage(uri: Uri, fileName: String): String {
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

    override suspend fun getThumbUri(url: String): Uri {
        val resizedSize = "_400x400"
        val fileExtension = ".jpg"
        val storageRef = Firebase.storage.reference
        val thumbRef = storageRef.child(url+resizedSize+fileExtension)
        val uri = thumbRef.downloadUrl.await()
        if (uri != null) {
            return uri
        } else {
            throw Exception("썸네일 불러오기 실패")
        }
    }

    override suspend fun getIDTokenByUser(user: FirebaseUser?): String {
        val resultGetIdToken = user?.getIdToken(true)?.await()?:throw Exception("유저 정보 가져오기 실패")
        val idToken = resultGetIdToken.token ?: throw Exception("ID 토큰 정보 가져오기 실패")
        return idToken
    }

    override suspend fun getAuthCredential(googleIdToken: String): AuthCredential {
        return GoogleAuthProvider.getCredential(googleIdToken, null)
    }

    override suspend fun signWithAuthCredential(authCredential: AuthCredential): FirebaseUser {
        val getUserTask = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
        val user = getUserTask.user?:throw Exception("유저 정보를 가져올 수 없습니다")
        return user
    }

    override suspend fun signInWithKakaoToken(token: OAuthToken): FirebaseUser {
        val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")

        val data = hashMapOf(
            "accessToken" to token.accessToken,
            "refreshToken" to token.refreshToken
        )

        val customTokenData = functions
                .getHttpsCallable("getCustomToken")
                .call(data)
                .await()
                .getData()?:throw Exception("토큰 정보 불러오기 실패")

        val user = FirebaseAuth.getInstance()
            .signInWithCustomToken(customTokenData.toString())
                .await()
                .user?:throw Exception("유저 정보 불러오기 실패")

        return user
    }

    override suspend fun signInWithUid(uid: String): FirebaseUser {
        val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")
        val data = hashMapOf(
            "uid" to uid
        )

        val customTokenData = functions
            .getHttpsCallable("getCustomTokenGP")
            .call(data)
            .await()
            .getData()?:throw Exception("토큰 정보 불러오기 실패")

        val user = FirebaseAuth.getInstance()
            .signInWithCustomToken(customTokenData.toString())
            .await()
            .user?:throw Exception("유저 정보 불러오기 실패")

        return user
    }

    override suspend fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser?:throw Exception("유저 정보 불러오기 실패")
    }

    override suspend fun deleteUser(user: FirebaseUser?): Boolean {
        if (user == null) {
            return false
        }
        user.delete().await()
        return true
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