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

    private val TAG = "AUTH_MODEL"

    suspend fun getGoogleIdTokenCredentialFlow(context: Context): Flow<DataState<String>> = flow {
        emit(DataState.Loading("자격 증명 정보 가져오는 중"))
        try {

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
                        emit(DataState.Success(idToken))
                    }
                }
                else -> {
                    emit(DataState.Failure("자격 증명 정보 타입이 다릅니다"))
                }
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.Default)

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

    suspend fun getIdTokenGoogleFlow(googleIdToken: String) = flow {
        emit(DataState.Loading("토큰 정보 가져오는 중"))
        try {
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdToken, null)
            val user = auth.signInWithCredential(firebaseCredential)
                .continueWith { task ->
                    if (task.isSuccessful) {
                        val result = task.result.user
                        return@continueWith result
                    } else {
                        val exception = task.exception
                        throw exception ?: Exception("Unknown error")
                    }
                }.await()
            emit(DataState.Success(Pair(getIdToken(user), LoginCode.NOTHING)))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.Default)

    suspend fun getIdTokenGoogle(googleIdToken: String): Pair<String, LoginCode> {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val result = auth.signInWithCredential(firebaseCredential).await()
        result.user?:throw Exception("Google Sign-in 실패")
        return Pair(getIdToken(result.user), LoginCode.NOTHING)
    }

    suspend fun getKaKaoTokenFlow(context: Context) = flow {
        emit(DataState.Loading("토큰 정보 가져오는 중"))
        emit(suspendCoroutine { continuation ->

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resume(DataState.Error(Exception(error)))
                } else if (token != null) {
                    continuation.resume(DataState.Success(token))
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            continuation.resume(DataState.Error(Exception(error)))
                            return@loginWithKakaoTalk
                        }
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }

        })
    }.flowOn(Dispatchers.Default)

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

    suspend fun getIdTokenKakaoFlow(accessToken: String, refreshToken: String) = flow {
        emit(DataState.Loading("ID 토큰을 가져오는 중입니다"))
        try {
            val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")

            // Create the arguments to the callable function.
            val data = hashMapOf(
                "accessToken" to accessToken,
                "refreshToken" to refreshToken
            )

            val customToken = functions
                .getHttpsCallable("getCustomToken")
                .call(data)
                .continueWith { task ->
                    if (task.isSuccessful) {
                        val result = task.result?.data.toString()
                        // 성공적으로 작업이 완료된 후에 추가 작업 수행
                        Log.d(TAG, "성공: "+result)
                        return@continueWith result
                    } else {
                        val exception = task.exception
                        // 에러 처리
                        Log.e(TAG, "실패: "+exception?.message)
                        throw exception ?: Exception("Unknown error")
                    }
                }.await()

            val user = auth.signInWithCustomToken(customToken)
                .continueWith { task ->
                    if (task.isSuccessful) {
                        val result = task.result.user
                        return@continueWith result
                    } else {
                        val exception = task.exception
                        throw exception ?: Exception("Unknown error")
                    }
                }.await()

            emit(DataState.Success(Pair(getIdToken(user), LoginCode.NOTHING)))

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }

    }.flowOn(Dispatchers.Default)

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

    suspend fun getIdTokenGPFlow(uid: String) = flow {
        emit(DataState.Loading("ID 토큰 가져오는 중"))
        try {
            val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")
            val data = hashMapOf(
                "uid" to uid
            )
            val customToken = functions
                .getHttpsCallable("getCustomTokenGP")
                .call(data)
                .continueWith { task ->
                    if (task.isSuccessful) {
                        val result = task.result?.data.toString()
                        // 성공적으로 작업이 완료된 후에 추가 작업 수행
                        Log.d(TAG, "성공: "+result)
                        return@continueWith result
                    } else {
                        val exception = task.exception
                        // 에러 처리
                        Log.e(TAG, "실패: "+exception?.message)
                        throw exception ?: Exception("Unknown error")
                    }
                }.await()

            val user = auth.signInWithCustomToken(customToken)
                .continueWith { task ->
                    if (task.isSuccessful) {
                        val result = task.result.user
                        return@continueWith result
                    } else {
                        val exception = task.exception
                        throw exception ?: Exception("Unknown error")
                    }
                }.await()

            emit(DataState.Success(Pair(getIdToken(user), LoginCode.NOTHING)))

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.Default)

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

    private fun continueIdTokenAuthUser(user: FirebaseUser?, continuation: Continuation<DataState<Pair<String, LoginCode>>>, loginCode: LoginCode = LoginCode.NOTHING) {
        if (user != null) {
            user.getIdToken(false)
                .addOnCompleteListener { taskIdToken ->
                    if (taskIdToken.isSuccessful) {
                        if (taskIdToken.result.token != null) {
                            continuation.resume(DataState.Success(Pair(taskIdToken.result.token!!, loginCode)))
                        } else {
                            continuation.resume(DataState.Error(Exception("ID 토큰 정보가 존재하지 않습니다")))
                        }
                    } else {
                        continuation.resume(DataState.Error(Exception("ID 토큰 정보 가져오기 실패")))
                    }
                }
        } else {
            continuation.resume(DataState.Error(Exception("유저 정보 가져오기 실패")))
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

    suspend fun getIdTokenFlow(user: FirebaseUser?=auth.currentUser): Flow<String> = flow {
        if (user != null) {
            val resultGetIdToken = user.getIdToken(true).await()
            resultGetIdToken.token ?: throw Exception("ID 토큰 정보 가져오기 실패")
            emit(resultGetIdToken.token!!)
        } else {
            throw Exception("유저 가져오기 실패")
        }
    }.flowOn(Dispatchers.IO)

}