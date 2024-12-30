package com.gusto.pikgoogoo.ui.subject.list

import android.util.Log
import androidx.lifecycle.*
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.Subject
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.data.repository.SubjectRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectListViewModel
@Inject
constructor(
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository
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
    val params = SubjectParameter(
        categoryId = 0,
        offset = 0,
        order = 0,
        searchWords = ""
    )
    var moreFlag = true

    private fun getSubjects() {
        viewModelScope.launch {
            if (params.offset == 0) {
                subjects.clear()
            }
            _subjectsData.value = DataState.Loading("리스트 가져오는 중")
            val result = try {
                subjectRepository.getSubjects(params.categoryId, params.order, params.offset)
            } catch (e: Exception) {
                _subjectsData.value = DataState.Error(e)
                return@launch
            }
            moreFlag = result.size > 0
            subjects.addAll(result)
            _subjectsData.value = DataState.Success(subjects)
        }
    }

    private fun getSubjectsBySearchWords() {
        viewModelScope.launch {
            if (params.offset == 0) {
                subjects.clear()
            }
            _subjectsData.value = DataState.Loading("리스트 가져오는 중")
            val result = try {
                subjectRepository.getSubjectsBySearchWords(params.categoryId, params.order, params.offset, params.searchWords)
            } catch (e: Exception) {
                _subjectsData.value = DataState.Error(e)
                return@launch
            }
            moreFlag = result.size > 0
            subjects.addAll(result)
            _subjectsData.value = DataState.Success(subjects)
        }
    }

    fun fetchSubjects() {
        if (params.searchWords.length > 0) {
            getSubjectsBySearchWords()
        } else {
            getSubjects()
        }
    }

    fun getGrade() {
        viewModelScope.launch {
            _gradeData.value = DataState.Loading("등급 정보 가져오는 중")
            val result = try {
                gradeRepository.getGradeFromLocal()
            } catch (e: Exception) {
                _gradeData.value = DataState.Error(e)
                return@launch
            }
            _gradeData.value = DataState.Success(result)
        }
    }

    data class SubjectParameter(
        var categoryId: Int,
        var offset: Int,
        var order: Int,
        var searchWords: String
    )

}