package com.gusto.pikgoogoo.ui.myinfo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.User
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val gradeRepository: GradeRepository
) : ViewModel() {

    private val _userData: MutableLiveData<DataState<User>> = MutableLiveData()
    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()
    private val _deleteState: MutableLiveData<DataState<String>> = MutableLiveData()

    val userData: LiveData<DataState<User>>
        get() = _userData
    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData
    val deleteState: LiveData<DataState<String>>
        get() = _deleteState

    fun fetchCurrentUserInfo(context: Context) {
        userRepository.getCurrentMorangUserFlow(context).onEach { dataState ->
            _userData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun fetchGradeList() {
        gradeRepository.getGradeFromLocalFlow().onEach { dataState ->
            _gradeData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun deleteUser() {
        userRepository.deleteUserFlow().onEach { dataState ->
            _deleteState.value = dataState
        }.launchIn(viewModelScope)
    }

}