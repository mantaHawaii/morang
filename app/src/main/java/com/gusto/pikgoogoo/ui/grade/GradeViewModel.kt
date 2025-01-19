package com.gusto.pikgoogoo.ui.grade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Grade
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
    private val gradeRepository: GradeRepository
) : ViewModel() {

    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()
    private val _gradeIconData: MutableLiveData<DataState<Int>> = MutableLiveData()

    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData
    val gradeIconData: LiveData<DataState<Int>>
        get() = _gradeIconData


    fun fetchGradeList() {
        viewModelScope.launch {
            gradeRepository.getGradeFromLocalFlow().onEach { dataState ->
                _gradeData.value = dataState
            }.launchIn(viewModelScope)
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