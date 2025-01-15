package com.gusto.pikgoogoo.ui.grade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradeViewModel
@Inject
constructor(
    private val gradeRepository: GradeRepository,
    private val authModel: AuthModel
) : ViewModel() {

    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()
    private val _gradeIconData: MutableLiveData<DataState<Int>> = MutableLiveData()

    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData
    val gradeIconData: LiveData<DataState<Int>>
        get() = _gradeIconData

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용하지 않는 함수")
    fun getGrade() {
        viewModelScope.launch {

            _gradeData.value = DataState.Loading("등급 정보 요청 중")
            val result = try {
                gradeRepository.getGradeFromLocal()
            } catch (e: Exception) {
                _gradeData.value = DataState.Error(e)
                return@launch
            }
            _gradeData.value = DataState.Success(result)

        }
    }

    fun fetchGradeList() {
        viewModelScope.launch {
            gradeRepository.getGradeFromLocalFlow().onEach { dataState ->
                _gradeData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용하지 않는 함수")
    fun setGradeIcon(gradeIcon: Int) {
        viewModelScope.launch {

            _gradeIconData.value = DataState.Loading("ID 토큰 정보 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _gradeIconData.value = DataState.Error(e)
                return@launch
            }

            _gradeIconData.value = DataState.Loading("등급 아이콘 설정 중")
            val result = try {
                gradeRepository.setGradeIcon(idToken, gradeIcon)
            } catch (e: Exception) {
                _gradeIconData.value = DataState.Error(e)
                return@launch
            }
            _gradeIconData.value = DataState.Success(result)
        }
    }

    fun updateUserGrade(gradeIcon: Int) {
        viewModelScope.launch {
            gradeRepository.upgradeGradeIconFlow(gradeIcon).onEach { dataState ->
                _gradeIconData.value = dataState
            }.launchIn(viewModelScope)
        }
    }



}