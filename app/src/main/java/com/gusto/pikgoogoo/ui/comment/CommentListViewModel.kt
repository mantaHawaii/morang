package com.gusto.pikgoogoo.ui.comment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Comment
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.CommentRepository
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentListViewModel
@Inject
constructor(
    private val commentRepository: CommentRepository,
    private val gradeRepository: GradeRepository,
    private val authModel: AuthModel,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _commentsData: MutableLiveData<DataState<List<Comment>>> = MutableLiveData()
    private val _commentOnRes: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _gradeData: MutableLiveData<DataState<List<Grade>>> = MutableLiveData()
    private val _editCommentRes: MutableLiveData<DataState<Pair<Int, String>>> = MutableLiveData()
    private val _deleteCommentRes: MutableLiveData<DataState<Int>> = MutableLiveData()
    private val _likeCommentRes: MutableLiveData<DataState<Int>> = MutableLiveData()

    val commentData: LiveData<DataState<List<Comment>>>
        get() = _commentsData
    val commentOnRes: LiveData<DataState<String>>
        get() = _commentOnRes
    val gradeData: LiveData<DataState<List<Grade>>>
        get() = _gradeData
    val editCommentRes: LiveData<DataState<Pair<Int, String>>>
        get() = _editCommentRes
    val deleteCommentRes: LiveData<DataState<Int>>
        get() = _deleteCommentRes
    val likeCommentRes: LiveData<DataState<Int>>
        get() = _likeCommentRes

    val comments = mutableListOf<Comment>()
    val params = CommentListParam(0, 0, 0, 0)
    var moreFlag = true

    fun getComments() {
        viewModelScope.launch {
            _commentsData.value = DataState.Loading("코멘트 가져오는 중")
            if (params.offset == 0) {
                comments.clear()
            }
            val result = try {
                commentRepository.getComments(params.subjectId, params.articleId, params.order, params.offset)
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }
            moreFlag = result.size > 0
            comments.addAll(result)
            _commentsData.value = DataState.Success(comments)
        }
    }

    fun commentOnArticle(articleId: Int, comment: String) {
        viewModelScope.launch {
            _commentOnRes.value = DataState.Loading("데이터 입력 중")
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _commentOnRes.value = DataState.Error(e)
                return@launch
            }
            val result = try {
                commentRepository.commentOnArticle(idToken, articleId, params.subjectId, comment)
            } catch (e: Exception) {
                _commentOnRes.value = DataState.Error(e)
                return@launch
            }
            _commentOnRes.value = DataState.Success(result)
        }
    }

    fun likeComment(commentId: Int) {
        viewModelScope.launch {
            _commentsData.value = DataState.Loading("통신 중")
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }
            try {
                commentRepository.likeComment(idToken, commentId)
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }
            val commentIndex = comments.indexOfFirst { it.id == commentId }
            if (commentIndex != -1) {
                comments[commentIndex].likeCount += 1
            }
            _commentsData.value = DataState.Success(comments)
        }
    }

    fun editComment(commentId: Int, comment: String) {
        viewModelScope.launch {
            _commentsData.value = DataState.Loading("통신 중")
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }
            try {
                commentRepository.editComment(idToken, commentId, comment)
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }
            val commentIndex = comments.indexOfFirst { it.id == commentId }
            if (commentIndex != -1) {
                comments[commentIndex].comment = comment
            }
            _commentsData.value = DataState.Success(comments)
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            _commentsData.value = DataState.Loading("통신 중")
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }
            try {
                commentRepository.deleteComment(idToken, commentId)
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }
            val commentIndex = comments.indexOfFirst { it.id == commentId }
            if (commentIndex != -1) {
                comments.removeAt(commentIndex)
            }
            _commentsData.value = DataState.Success(comments)
        }
    }

    fun getGrade() {
        viewModelScope.launch {
            _gradeData.value = DataState.Loading("통신 중")
            val grades = try {
                gradeRepository.getGradeFromLocal()
            } catch (e: Exception) {
                _gradeData.value = DataState.Error(e)
                return@launch
            }
            _gradeData.value = DataState.Success(grades)
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