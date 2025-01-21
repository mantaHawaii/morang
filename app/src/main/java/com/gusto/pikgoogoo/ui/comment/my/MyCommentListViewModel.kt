package com.gusto.pikgoogoo.ui.comment.my

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Comment
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.repository.CommentRepository
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCommentListViewModel
@Inject
constructor(
    private val commentRepository: CommentRepository,
    private val gradeRepository: GradeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _commentData: MutableLiveData<DataState<List<Comment>>> = MutableLiveData()
    private val _responseData: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()

    val commentData: LiveData<DataState<List<Comment>>>
        get() = _commentData
    val responseData: LiveData<DataState<String>>
        get() = _responseData
    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData

    var params = MyCommentParam(0, 0)

    fun fetchMyComments(context: Context) {
        viewModelScope.launch {
            commentRepository.fetchMyComments(context, params.order, params.offset)
                .onEach { dataState ->
                    _commentData.value = dataState
                }.launchIn(viewModelScope)
        }
    }

    fun getUid(context: Context): Int {
        return try {
            userRepository.getUidFromShareRef(context)
        } catch (e: Exception) {
            0
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

    data class MyCommentParam(
        var order: Int,
        var offset: Int
    )

}