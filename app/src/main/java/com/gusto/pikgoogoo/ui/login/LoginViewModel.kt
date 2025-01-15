package com.gusto.pikgoogoo.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.LoginCode
import com.gusto.pikgoogoo.data.User
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import com.kakao.sdk.auth.model.OAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val authModel: AuthModel
): ViewModel() {

    private val _userData: MutableLiveData<DataState<User>> = MutableLiveData()
    private val _registerState: MutableLiveData<DataState<Int>> = MutableLiveData()

    private val _googleTokenState: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _kakaoTokenState: MutableLiveData<DataState<OAuthToken>> = MutableLiveData()
    private val _idTokenState: MutableLiveData<DataState<Pair<String, LoginCode>>> = MutableLiveData()

    val userData: LiveData<DataState<User>>
        get() = _userData

    val registerState: LiveData<DataState<Int>>
        get() = _registerState

    @Deprecated("이 변수는 더 이상 사용되지 않습니다")
    val googleTokenState: LiveData<DataState<String>>
        get() = _googleTokenState

    @Deprecated("이 변수는 더 이상 사용되지 않습니다")
    val kakaoTokenState: LiveData<DataState<OAuthToken>>
        get() = _kakaoTokenState

    @Deprecated("이 변수는 더 이상 사용되지 않습니다")
    val idTokenState: LiveData<DataState<Pair<String, LoginCode>>>
        get() = _idTokenState


    fun registerUser(nickname: String) {
        viewModelScope.launch {
            userRepository.registerUserFlow(nickname).onEach { dataState ->
                _registerState.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getMorangUserWithGoogleSignIn(context: Context) {
        viewModelScope.launch {
            userRepository.getMorangUserWithGoogleFlow(context).onEach { dataState ->
                _userData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getIdTokenWithKakaoSignIn(context: Context) {
        viewModelScope.launch {
            userRepository.getMorangUserWithKakaoFlow(context).onEach { dataState ->
                _userData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getIdTokenWithGP(uid: String) {
        viewModelScope.launch {
            userRepository.getMorangUserWithGPFlow(uid).onEach { dataState ->
                _userData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun putId(uid: Int) {
        viewModelScope.launch {
            userRepository
        }
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    fun getGoogleIdToken(context: Context) {
        viewModelScope.launch {
            _googleTokenState.value = DataState.Loading("구글 토큰 가져오는 중")
            val result = try {
                authModel.getGoogleIdTokenCredential(context)
            } catch (e: Exception) {
                _googleTokenState.value = DataState.Error(e)
                return@launch
            }
            _googleTokenState.value = DataState.Success(result)
        }
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    fun getKakaoToken(context: Context) {
        viewModelScope.launch {
            _kakaoTokenState.value = DataState.Loading("카카오 토큰 가져오는 중")
            val oAuthToken = try {
                authModel.getKaKaoToken(context)
            } catch (e: Exception) {
                _kakaoTokenState.value = DataState.Error(e)
                return@launch
            }
            _kakaoTokenState.value = DataState.Success(oAuthToken)
        }
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    fun getTokenIdKakao(token: OAuthToken) {
        viewModelScope.launch {
            _idTokenState.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenKakao(token.accessToken, token.refreshToken)
            } catch (e: Exception) {
                _idTokenState.value = DataState.Error(e)
                return@launch
            }
            _idTokenState.value = DataState.Success(idToken)
        }
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    fun getTokenIdGoogle(token: String) {
        viewModelScope.launch {
            _idTokenState.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenGoogle(token)
            } catch (e: Exception) {
                _idTokenState.value = DataState.Error(e)
                return@launch
            }
            _idTokenState.value = DataState.Success(idToken)
        }
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    fun getTokenIdGP(uid: String) {
        viewModelScope.launch {
            _idTokenState.value = DataState.Loading("ID 토큰 가져오는 중")
            val result = try {
                authModel.getIdTokenGP(uid)
            } catch (e: Exception) {
                _idTokenState.value = DataState.Error(e)
                return@launch
            }
            _idTokenState.value = DataState.Success(result)
        }
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    fun getTokenIdAuthUser(registerFlag: Boolean) {
        viewModelScope.launch {
            _idTokenState.value = DataState.Loading("ID 토큰 가져오는 중")
            val pair = try {
                authModel.getIdTokenAuthUser(registerFlag)
            } catch (e: Exception) {
                _idTokenState.value = DataState.Error(e)
                return@launch
            }
            _idTokenState.value = DataState.Success(pair)
        }
    }

    @Deprecated("이 함수는 더 이상 사용되지 않습니다")
    fun getUserByToken() {
        viewModelScope.launch {

            _userData.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _userData.value = DataState.Error(e)
                return@launch
            }

            _userData.value = DataState.Loading("유저 정보 가져오는 중")
            val user = try {
                userRepository.getUserByToken(idToken)
            } catch (e: Exception) {
                _userData.value = DataState.Error(e)
                return@launch
            }
            _userData.value = DataState.Success(user)
        }
    }

}