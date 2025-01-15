package com.gusto.pikgoogoo.datasource

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken

interface FirebaseDataSource {
    suspend fun storeImage(uri: Uri, fileName: String): String
    suspend fun getThumbUri(url: String): Uri
    suspend fun getIDTokenByUser(user: FirebaseUser?): String
    suspend fun getAuthCredential(googleIdToken: String): AuthCredential
    suspend fun signWithAuthCredential(authCredential: AuthCredential): FirebaseUser
    suspend fun signInWithKakaoToken(token: OAuthToken): FirebaseUser
    suspend fun signInWithUid(uid: String): FirebaseUser
    suspend fun getCurrentUser(): FirebaseUser
    suspend fun deleteUser(user: FirebaseUser?): Boolean
}