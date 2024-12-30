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

    private val subjects = mutableListOf<Subject>()
    var moreFlag = true
    val params = BookmarkedSubjectListParameter(0, 0)

    fun getBookmarkedSubjects(context: Context) {
        viewModelScope.launch {
            val userId = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _subjectsData.value = DataState.Error(e)
                return@launch
            }
            if (params.offset == 0) {
                subjects.clear()
            }
            val result = try {
                subjectRepository.getBookmarkedSubjects(userId, params.order, params.offset)
            } catch (e: Exception) {
                _subjectsData.value = DataState.Error(e)
                return@launch
            }
            moreFlag = result.size > 0
            subjects.addAll(result)
            _subjectsData.value = DataState.Success(result)
        }
    }

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

    data class BookmarkedSubjectListParameter(
        var order: Int,
        var offset: Int
    )

}