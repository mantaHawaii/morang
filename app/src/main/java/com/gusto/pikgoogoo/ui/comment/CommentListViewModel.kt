package com.gusto.pikgoogoo.ui.comment

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
class CommentListViewModel
@Inject
constructor(
    private val commentRepository: CommentRepository,
    private val gradeRepository: GradeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _commentsData: MutableLiveData<DataState<List<Comment>>> = MutableLiveData()
    private val _commentOnRes: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()

    val commentData: LiveData<DataState<List<Comment>>>
        get() = _commentsData
    val commentOnRes: LiveData<DataState<String>>
        get() = _commentOnRes
    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData

    val params = CommentListParam(0, 0, 0, 0)

    fun fetchComments() {
        viewModelScope.launch {
            commentRepository.fetchCommentsFlow(params.subjectId, params.articleId, params.order, params.offset)
                .onEach { dataState ->
                    _commentsData.value = dataState
                }.launchIn(viewModelScope)
        }
    }

    fun postCommentToArticle(articleId: Int, comment: String) {
        viewModelScope.launch {
            commentRepository.postCommentToArticleFlow(articleId, params.subjectId, comment)
                .onEach { dataState ->
                    _commentOnRes.value = dataState
                }.launchIn(viewModelScope)
        }
    }

    fun submitCommentLike(commentId: Int) {
        viewModelScope.launch {
            commentRepository.likeCommentFlow(commentId).onEach { dataState ->
                _commentsData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun updateComment(commentId: Int, comment: String) {
        viewModelScope.launch {
            commentRepository.updateCommentFlow(commentId, comment).onEach { dataState ->
                _commentsData.value = dataState
            }.launchIn(viewModelScope)
        }
    }
    fun removeComment(commentId: Int) {
        viewModelScope.launch {
            commentRepository.removeCommentFlow(commentId).onEach { dataState ->
                _commentsData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun fetchGradeList() {
        viewModelScope.launch {
            gradeRepository.getGradeFromLocalFlow().onEach { dataState ->
                _gradeData.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    fun getMyUid(context: Context): Int {
        return userRepository.getUidFromShareRef(context)
    }

    data class CommentListParam(
        var articleId: Int,
        var subjectId: Int,
        var offset: Int,
        var order: Int
    )

}