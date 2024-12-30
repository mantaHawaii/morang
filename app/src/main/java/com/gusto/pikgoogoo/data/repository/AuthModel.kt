package com.gusto.pikgoogoo.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.LoginCode
import com.gusto.pikgoogoo.util.DataState
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthModel
@Inject
constructor(
    private val auth: FirebaseAuth
) {

    suspend fun getGoogleIdTokenCredential(context: Context): String {

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(UUID.randomUUID().toString())
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(context)

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )

        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    return idToken
                } else {
                    throw Exception("Unknown error")
                }
            }
            else -> {
                throw Exception("자격 증명 정보 타입이 다릅니다")
            }
        }
    }
    
    suspend fun getIdTokenGoogle(googleIdToken: String): Pair<String, LoginCode> {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val getUserTask = auth.signInWithCredential(firebaseCredential).await()
        val user = getUserTask.user?:throw Exception("유저 정보를 가져올 수 없습니다")
        return Pair(getIdToken(user), LoginCode.NOTHING)
    }

    suspend fun getKaKaoToken(context: Context): OAuthToken {
        return suspendCoroutine { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    continuation.resume(token)
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            continuation.resumeWithException(error)
                        }
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        continuation.resume(token)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }

    suspend fun getIdTokenKakao(accessToken: String, refreshToken: String): Pair<String, LoginCode> {
        val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")

        val data = hashMapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )

        val customToken = withContext(Dispatchers.Default) {
                functions
                    .getHttpsCallable("getCustomToken")
                    .call(data)
                    .await()
        }?.data.toString()

        val user = withContext(Dispatchers.Default) {
                auth.signInWithCustomToken(customToken)
                    .await()
        }?.user

        return Pair(getIdToken(user), LoginCode.NOTHING)

    }

    suspend fun getIdTokenGP(uid: String): Pair<String, LoginCode> {

        val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")
        val data = hashMapOf(
            "uid" to uid
        )

        val customToken = try {
            val result = withContext(Dispatchers.Default) {
                functions
                    .getHttpsCallable("getCustomTokenGP")
                    .call(data)
                    .await()
            }
            result?.data.toString()
        } catch (e: Exception) {
            throw e
        }

        val user = try {
            val result = withContext(Dispatchers.Default) {
                auth.signInWithCustomToken(customToken)
                    .await()
            }
            result?.user
        } catch (e: Exception) {
            throw e
        }

        return Pair(getIdToken(user), LoginCode.NOTHING)

    }

    suspend fun getIdTokenAuthUser(registerFlag: Boolean): Pair<String, LoginCode> {
        try {
            val token = getIdToken(auth.currentUser)
            if (registerFlag) {
                return Pair(token, LoginCode.REIGSTER)
            } else {
                return Pair(token, LoginCode.NOTHING)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getIdToken(user: FirebaseUser?=auth.currentUser): String {
        if (user != null) {
            val resultGetIdToken = user.getIdToken(true).await()
            resultGetIdToken.token ?: throw Exception("ID 토큰 정보 가져오기 실패")
            return resultGetIdToken.token!!
        } else {
            throw Exception("유저 정보 가져오기 실패")
        }
    }
}
