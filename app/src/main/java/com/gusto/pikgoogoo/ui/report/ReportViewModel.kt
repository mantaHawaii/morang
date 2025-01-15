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

    fun getReportReasons(type: Int) {
        viewModelScope.launch {
            _reportData.value = DataState.Loading("신고 사유 목록 가져오는 중")
            val result = try {
                reportRepository.getReportReasons(type)
            } catch (e: Exception) {
                _reportData.value = DataState.Error(e)
                return@launch
            }
            _reportData.value = DataState.Success(result)
        }
    }

    fun reportArticle(context: Context, articleId: Int, reportId: Int) {
        viewModelScope.launch {

            _messageData.value = DataState.Loading("유저 아이디 가져오는 중")
            val uid = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _messageData.value = DataState.Error(e)
                return@launch
            }

            _messageData.value = DataState.Loading("서버에 항목 신고 요청 중")
            val result = try {
                reportRepository.reportArticle(articleId, reportId, uid)
            } catch (e: Exception) {
                _messageData.value = DataState.Error(e)
                return@launch
            }
            _messageData.value = DataState.Success(result)
        }
    }

    fun reportComment(context: Context, commentId: Int, reportId: Int) {
        viewModelScope.launch {

            _messageData.value = DataState.Loading("유저 아이디 가져오는 중")
            val uid = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _messageData.value = DataState.Error(e)
                return@launch
            }

            _messageData.value = DataState.Loading("서버에 코멘트 신고 요청 중")
            val result = try {
                reportRepository.reportComment(commentId, reportId, uid)
            } catch (e: Exception) {
                _messageData.value = DataState.Error(e)
                return@launch
            }
            _messageData.value = DataState.Success(result)
        }
    }

}