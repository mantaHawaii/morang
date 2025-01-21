package com.gusto.pikgoogoo.ui.report

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.ReportReason
import com.gusto.pikgoogoo.data.repository.ReportRepository
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject
constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _reportData: MutableLiveData<DataState<List<ReportReason>>> = MutableLiveData()
    private val _messageData: MutableLiveData<DataState<String>> = MutableLiveData()

    val reportData: LiveData<DataState<List<ReportReason>>>
        get() = _reportData
    val messageData: LiveData<DataState<String>>
        get() = _messageData

    fun fetchReportReason(type: Int) {
        reportRepository.fetchReportReasonsFlow(type).onEach { dataState ->
            _reportData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun reportArticle(context: Context, articleId: Int, reportId: Int) {
        reportRepository.reportArticleFlow(context, articleId, reportId)
            .onEach { dataState ->
                _messageData.value = dataState
            }.launchIn(viewModelScope)
    }

    fun reportComment(context: Context, commentId: Int, reportId: Int) {
        reportRepository.reportCommentFlow(context, commentId, reportId)
            .onEach { dataState ->
                _messageData.value = dataState
            }.launchIn(viewModelScope)
    }

}