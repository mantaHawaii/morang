package com.gusto.pikgoogoo.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val authModel: AuthModel
) : ViewModel() {

    private val _responseData: MutableLiveData<DataState<String>> = MutableLiveData()

    val responseData: LiveData<DataState<String>>
        get() = _responseData

    @Deprecated("2025-01-15부터 사용하지 않는 함수")
    fun editProfileNickname(nickname: String) {
        viewModelScope.launch {

            _responseData.value = DataState.Loading("ID 토큰 정보 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _responseData.value = DataState.Error(e)
                return@launch
            }

            _responseData.value = DataState.Loading("서버에 닉네임 변경 요청 중")
            val result = try {
                userRepository.editUser(idToken, nickname)
            } catch (e: Exception) {
                _responseData.value = DataState.Error(e)
                return@launch
            }
            _responseData.value = DataState.Success(result)
        }
    }

    fun editUserNickname(nickname: String) {
        viewModelScope.launch {
            userRepository.editUserFlow(nickname).onEach { dataState ->
                _responseData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

}