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
    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()

    val subjectsData: LiveData<DataState<List<Subject>>>
        get() = _subjectsData
    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData

    val params = SubjectParameter(
        categoryId = 0,
        offset = 0,
        order = 0,
        searchWords = ""
    )

    private fun fetchSubjectDefault() {
        subjectRepository.fetchSubjectsFlow(params.categoryId, params.order, params.offset)
            .onEach { dataState ->
                _subjectsData.value = dataState
            }.launchIn(viewModelScope)
    }

    private fun fetchSubjectBySearchWords() {
        subjectRepository.fetchSubjectsBySearchWordsFlow(
            params.categoryId,
            params.order,
            params.offset,
            params.searchWords
        ).onEach { dataState ->
            _subjectsData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun fetchSubjects() {
        if (params.searchWords.length > 0) {
            fetchSubjectBySearchWords()
        } else {
            fetchSubjectDefault()
        }
    }

    fun fetchGrade() {
        gradeRepository.getGradeFromLocalFlow().onEach { dataState ->
            _gradeData.value = dataState
        }.launchIn(viewModelScope)
    }

    data class SubjectParameter(
        var categoryId: Int,
        var offset: Int,
        var order: Int,
        var searchWords: String
    )

}