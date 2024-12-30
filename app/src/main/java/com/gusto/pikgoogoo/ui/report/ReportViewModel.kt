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
            _messageData.value = DataState.Loading()
            val uid = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _messageData.value = DataState.Error(e)
                return@launch
            }
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
            _messageData.value = DataState.Loading()
            val uid = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _messageData.value = DataState.Error(e)
                return@launch
            }
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