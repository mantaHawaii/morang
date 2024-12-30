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
    private val _registerState: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _googleTokenState: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _kakaoTokenState: MutableLiveData<DataState<OAuthToken>> = MutableLiveData()
    private val _idTokenState: MutableLiveData<DataState<Pair<String, LoginCode>>> = MutableLiveData()

    val userData: LiveData<DataState<User>>
        get() = _userData
    val registerState: LiveData<DataState<String>>
        get() = _registerState
    val googleTokenState: LiveData<DataState<String>>
        get() = _googleTokenState
    val kakaoTokenState: LiveData<DataState<OAuthToken>>
        get() = _kakaoTokenState
    val idTokenState: LiveData<DataState<Pair<String, LoginCode>>>
        get() = _idTokenState

    fun requestRegister(token: String, nickname: String) {
        viewModelScope.launch {
            _registerState.value = DataState.Loading("유저 등록 중")
            val result = try {
                userRepository.registerUser(token, nickname)
            } catch (e: Exception) {
                _registerState.value = DataState.Error(e)
                return@launch
            }
            _registerState.value = DataState.Success(result)
        }
    }

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

    fun getTokenIdKakao(token: OAuthToken) {
        viewModelScope.launch {
            _idTokenState.value = DataState.Loading("ID토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenKakao(token.accessToken, token.refreshToken)
            } catch (e: Exception) {
                _idTokenState.value = DataState.Error(e)
                return@launch
            }
            _idTokenState.value = DataState.Success(idToken)
        }
    }

    fun getTokenIdGoogle(token: String) {
        viewModelScope.launch {
            _idTokenState.value = DataState.Loading("ID토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenGoogle(token)
            } catch (e: Exception) {
                _idTokenState.value = DataState.Error(e)
                return@launch
            }
            _idTokenState.value = DataState.Success(idToken)
        }
    }

    fun getTokenIdGP(uid: String) {
        viewModelScope.launch {
            _idTokenState.value = DataState.Loading("ID토큰 가져오는 중")
            val result = try {
                authModel.getIdTokenGP(uid)
            } catch (e: Exception) {
                _idTokenState.value = DataState.Error(e)
                return@launch
            }
            _idTokenState.value = DataState.Success(result)
        }
    }

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

    fun getUserByToken() {
        viewModelScope.launch {
            _userData.value = DataState.Loading("유저 정보 가져오는 중")
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _userData.value = DataState.Error(e)
                return@launch
            }
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