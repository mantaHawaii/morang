package com.gusto.pikgoogoo.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.LoginCode
import com.gusto.pikgoogoo.data.User
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
    private val userRepository: UserRepository
): ViewModel() {

    private val _userData: MutableLiveData<DataState<User>> = MutableLiveData()
    private val _registerState: MutableLiveData<DataState<Int>> = MutableLiveData()

    val userData: LiveData<DataState<User>>
        get() = _userData
    val registerState: LiveData<DataState<Int>>
        get() = _registerState


    fun registerUser(nickname: String) {
        userRepository.registerUserFlow(nickname).onEach { dataState ->
            _registerState.value = dataState
        }.launchIn(viewModelScope)
    }

    fun getMorangUserWithGoogleSignIn(context: Context) {
        userRepository.getMorangUserWithGoogleFlow(context).onEach { dataState ->
            _userData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun getIdTokenWithKakaoSignIn(context: Context) {
        userRepository.getMorangUserWithKakaoFlow(context).onEach { dataState ->
            _userData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun getIdTokenWithGP(uid: String) {
        userRepository.getMorangUserWithGPFlow(uid).onEach { dataState ->
            _userData.value = dataState
        }.launchIn(viewModelScope)
    }

}