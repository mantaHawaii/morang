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
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    val userData: LiveData<DataState<User>>
        get() = _userData
    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData

    fun getMyUserData(context: Context){
        viewModelScope.launch {
            val uid = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _userData.value = DataState.Error(e)
                return@launch
            }
            val user = try {
                userRepository.getUserById(uid)
            } catch (e: Exception) {
                _userData.value = DataState.Error(e)
                return@launch
            }
            _userData.value = DataState.Success(user)
        }
    }

    fun getGrade() {
        viewModelScope.launch {
            val grades = try {
                gradeRepository.getGradeFromLocal()
            } catch (e: Exception){
                _gradeData.value = DataState.Error(e)
                return@launch
            }
            _gradeData.value = DataState.Success(grades)
        }
    }

}