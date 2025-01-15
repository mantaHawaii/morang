package com.gusto.pikgoogoo.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.gusto.pikgoogoo.BuildConfig
import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.LoginCode
import com.gusto.pikgoogoo.data.User
import com.gusto.pikgoogoo.data.UserMapper
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.datasource.GoogleDataSource
import com.gusto.pikgoogoo.datasource.KakaoDataSource
import com.gusto.pikgoogoo.util.DataState
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthModel
@Inject
constructor(
    private val auth: FirebaseAuth,
    private val firebaseDataSource: FirebaseDataSource,
    private val googleDataSource: GoogleDataSource,
    private val kakaoDataSource: KakaoDataSource,
    private val userMapper: UserMapper,
    private val webService: WebService
) {

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

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    suspend fun getGoogleIdTokenCredential(context: Context): String {

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
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

    @Deprecated("이 함수는 더 이상 사용되지 않습니다 getIdTokenGoogleFlow를 사용하세요", ReplaceWith("getIdTokenGoogleFlow(context)"))
    suspend fun getIdTokenGoogle(googleIdToken: String): Pair<String, LoginCode> {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val getUserTask = auth.signInWithCredential(firebaseCredential).await()
        val user = getUserTask.user?:throw Exception("유저 정보를 가져올 수 없습니다")
        return Pair(getIdTokenByUser(user), LoginCode.NOTHING)
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
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

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    suspend fun getIdTokenKakao(accessToken: String, refreshToken: String): Pair<String, LoginCode> {
        val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")

        val data = hashMapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )

        val customTokenData = withContext(Dispatchers.Default) {
            functions
                .getHttpsCallable("getCustomToken")
                .call(data)
                .await()
        }.getData()?:throw Exception("토큰 정보 불러오기 실패")

        val user = withContext(Dispatchers.Default) {
            auth.signInWithCustomToken(customTokenData.toString())
                .await()
        }?.user?:throw Exception("유저 정보 불러오기 실패")

        return Pair(getIdTokenByUser(user), LoginCode.NOTHING)

    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    suspend fun getIdTokenGP(uid: String): Pair<String, LoginCode> {

        val functions : FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")
        val data = hashMapOf(
            "uid" to uid
        )

        val customTokenData = withContext(Dispatchers.Default) {
            functions
                .getHttpsCallable("getCustomTokenGP")
                .call(data)
                .await()
        }.getData()?:throw Exception("토큰 정보 불러오기 실패")

        val user = withContext(Dispatchers.Default) {
            auth.signInWithCustomToken(customTokenData.toString())
                .await()
        }?.user?:throw Exception("유저 정보 불러오기 실패")

        return Pair(getIdTokenByUser(user), LoginCode.NOTHING)

    }

    suspend fun getIdTokenAuthUser(registerFlag: Boolean): Pair<String, LoginCode> {
        val token = getIdTokenByUser(auth.currentUser)
        if (registerFlag) {
            return Pair(token, LoginCode.REIGSTER)
        } else {
            return Pair(token, LoginCode.NOTHING)
        }
    }

    suspend fun getIdTokenByUser(user: FirebaseUser?=auth.currentUser): String {
        if (user != null) {
            val resultGetIdToken = user.getIdToken(true).await()
            val idToken = resultGetIdToken.token ?: throw Exception("ID 토큰 정보 가져오기 실패")
            return idToken
        } else {
            throw Exception("유저 정보 가져오기 실패")
        }
    }

    fun getIdTokenByUserFlow(user: FirebaseUser?=auth.currentUser) = flow {
        emit(DataState.Loading("ID토큰 가져오는 중"))
        try {
            val idToken = firebaseDataSource.getIDTokenByUser(user)
            emit(DataState.Success(idToken))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}