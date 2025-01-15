package com.gusto.pikgoogoo.ui.withdrawal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawalViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val authModel: AuthModel
) : ViewModel() {

    private val _responseData: MutableLiveData<DataState<String>> = MutableLiveData()
    val responseData: LiveData<DataState<String>> get() = _responseData

    private val _deleteState: MutableLiveData<DataState<String>> = MutableLiveData()
    val deleteState: LiveData<DataState<String>> get() = _deleteState

    @Deprecated("2025-01-15이후로 사용하지 않습니다")
    fun requestWithdrawal() {
        viewModelScope.launch {
            _responseData.value = DataState.Loading("탈퇴 처리 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _responseData.value = DataState.Error(e)
                return@launch
            }
            try {
                userRepository.deleteUserFromServer(idToken)
            } catch (e: Exception) {
                _responseData.value = DataState.Error(e)
                return@launch
            }
            _responseData.value = DataState.Loading("Firebase 정보 삭제 중")
            val result = try {
                userRepository.requestWithdrawal()
            } catch (e: Exception) {
                _responseData.value = DataState.Error(e)
                return@launch
            }
            _responseData.value = DataState.Success(result)
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUserFlow().onEach { dataState ->
                _deleteState.value = dataState
            }.launchIn(viewModelScope)
        }
    }
}