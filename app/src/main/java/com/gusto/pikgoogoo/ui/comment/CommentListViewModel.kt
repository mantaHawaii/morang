package com.gusto.pikgoogoo.ui.comment

import android.content.Context
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
    private val authModel: AuthModel,
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

    @Deprecated("2025-01-15 이후로 사용되지 않는 변수입니다")
    val comments = mutableListOf<Comment>()
    @Deprecated("2025-01-15 이후로 사용되지 않는 변수입니다")
    var moreFlag = true

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

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용되지 않는 함수입니다")
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

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용되지 않는 함수입니다")
    fun commentOnArticle(articleId: Int, comment: String) {
        viewModelScope.launch {

            _commentsData.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _commentOnRes.value = DataState.Error(e)
                return@launch
            }

            _commentsData.value = DataState.Loading("서버에 코멘트 쓰기 요청 중")
            val result = try {
                commentRepository.commentOnArticle(idToken, articleId, params.subjectId, comment)
            } catch (e: Exception) {
                _commentOnRes.value = DataState.Error(e)
                return@launch
            }
            _commentOnRes.value = DataState.Success(result)
        }
    }

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용되지 않는 함수입니다")
    fun likeComment(commentId: Int) {
        viewModelScope.launch {

            _commentsData.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }

            _commentsData.value = DataState.Loading("서버에 코멘트 좋아요 요청 중")
            try {
                commentRepository.likeComment(idToken, commentId)
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }

            _commentsData.value = DataState.Loading("클라이언트 데이터 변경 중")
            val commentIndex = comments.indexOfFirst { it.id == commentId }
            if (commentIndex != -1) {
                comments[commentIndex].likeCount += 1
            }
            _commentsData.value = DataState.Success(comments)
        }
    }

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용되지 않는 함수입니다")
    fun editComment(commentId: Int, comment: String) {
        viewModelScope.launch {

            _commentsData.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }

            _commentsData.value = DataState.Loading("서버에 코멘트 수정 요청 중")
            try {
                commentRepository.editComment(idToken, commentId, comment)
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }

            _commentsData.value = DataState.Loading("클라이언트 데이터 변경 중")
            val commentIndex = comments.indexOfFirst { it.id == commentId }
            if (commentIndex != -1) {
                comments[commentIndex].comment = comment
            }
            _commentsData.value = DataState.Success(comments)
        }
    }

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용되지 않는 함수입니다")
    fun deleteComment(commentId: Int) {
        viewModelScope.launch {

            _commentsData.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }

            _commentsData.value = DataState.Loading("서버에 코멘트 삭제 요청 중")
            try {
                commentRepository.deleteComment(idToken, commentId)
            } catch (e: Exception) {
                _commentsData.value = DataState.Error(e)
                return@launch
            }

            _commentsData.value = DataState.Loading("클라이언트 데이터 변경 중")
            val commentIndex = comments.indexOfFirst { it.id == commentId }
            if (commentIndex != -1) {
                comments.removeAt(commentIndex)
            }
            _commentsData.value = DataState.Success(comments)
        }
    }

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용되지 않는 함수")
    fun getGrade() {
        viewModelScope.launch {
            _gradeData.value = DataState.Loading("등급 정보 가져오는 중")
            val grades = try {
                gradeRepository.getGradeFromLocal()
            } catch (e: Exception) {
                _gradeData.value = DataState.Error(e)
                return@launch
            }
            _gradeData.value = DataState.Success(grades)
        }
    }

}