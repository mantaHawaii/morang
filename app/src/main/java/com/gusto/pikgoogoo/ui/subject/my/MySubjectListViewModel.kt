package com.gusto.pikgoogoo.ui.subject.my

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.Subject
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.data.repository.SubjectRepository
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySubjectListViewModel
@Inject
constructor(
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _subjectsData: MutableLiveData<DataState<List<Subject>>> = MutableLiveData()
    private val _responseData: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()

    val subjectsData: LiveData<DataState<List<Subject>>>
        get() = _subjectsData
    val responseData: LiveData<DataState<String>>
        get() = _responseData
    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData

    val subjects = mutableListOf<Subject>()
    var moreFlag = true
    val params = MySubjectListParameter(0, 0)

    fun getMySubjects(context: Context) {
        viewModelScope.launch {
            if (params.offset == 0) {
                subjects.clear()
            }
            val uid = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _subjectsData.value = DataState.Error(e)
                return@launch
            }
            val result = try {
                subjectRepository.getMySubjects(uid, params.order, params.offset)
            } catch (e: Exception) {
                _subjectsData.value = DataState.Error(e)
                return@launch
            }
            moreFlag = result.size > 0
            subjects.addAll(result)
            _subjectsData.value = DataState.Success(subjects)
        }
    }

    fun getGrade() {
        viewModelScope.launch {
            gradeRepository.getGradeFromLocalFlow()
                .onEach { dataState ->
                    _gradeData.value = dataState
                }.launchIn(viewModelScope)
        }
    }

    data class MySubjectListParameter(
        var offset: Int,
        var order: Int
    )

}