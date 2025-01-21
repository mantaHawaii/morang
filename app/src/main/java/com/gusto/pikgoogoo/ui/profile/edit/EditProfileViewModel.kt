package com.gusto.pikgoogoo.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _responseData: MutableLiveData<DataState<String>> = MutableLiveData()

    val responseData: LiveData<DataState<String>>
        get() = _responseData

    fun editUserNickname(nickname: String) {
        userRepository.editUserFlow(nickname).onEach { dataState ->
            _responseData.value = dataState
        }.launchIn(viewModelScope)
    }

}