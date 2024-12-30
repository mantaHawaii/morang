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
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    fun getGrade() {
        viewModelScope.launch {
            val result = try {
                gradeRepository.getGradeFromLocal()
            } catch (e: Exception) {
                _gradeData.value = DataState.Error(e)
                return@launch
            }
            _gradeData.value = DataState.Success(result)
        }
    }

    fun setGradeIcon(gradeIcon: Int) {
        viewModelScope.launch {
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _gradeIconData.value = DataState.Error(e)
                return@launch
            }
            val result = try {
                gradeRepository.setGradeIcon(idToken, gradeIcon)
            } catch (e: Exception) {
                _gradeIconData.value = DataState.Error(e)
                return@launch
            }
            _gradeIconData.value = DataState.Success(result)
        }
    }

}