package com.gusto.pikgoogoo.ui.subject.bookmarked

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkedSubjectListViewModel
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

    val params = BookmarkedSubjectListParameter(0, 0)

    fun fetchBookmarkedSubjects(context: Context) {
        subjectRepository.fetchBookmarkedSubjectsFlow(context, params.order, params.offset)
            .onEach { dataState ->
                _subjectsData.value = dataState
            }.launchIn(viewModelScope)
    }

    fun fetchGrade() {
        gradeRepository.getGradeFromLocalFlow().onEach { dataState ->
            _gradeData.value = dataState
        }.launchIn(viewModelScope)
    }

    data class BookmarkedSubjectListParameter(
        var order: Int,
        var offset: Int
    )

}