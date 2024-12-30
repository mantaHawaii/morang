package com.gusto.pikgoogoo.ui.article.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.VoteHistory
import com.gusto.pikgoogoo.data.repository.ArticleRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleChartViewModel
@Inject
constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _voteHistoryData: MutableLiveData<DataState<List<VoteHistory>>> = MutableLiveData()
    private val _createdDate: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _term: MutableLiveData<Pair<String, String>> = MutableLiveData()

    val voteHistoryData: LiveData<DataState<List<VoteHistory>>>
        get() = _voteHistoryData
    val createdDate: LiveData<DataState<String>>
        get() = _createdDate
    val term: LiveData<Pair<String, String>>
        get() = _term

    fun getVoteHistory(articleId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            val result = try {
                articleRepository.getVoteHistory(articleId, startDate, endDate)
            } catch (e: Exception) {
                _voteHistoryData.value = DataState.Error(e)
                return@launch
            }
            _voteHistoryData.value = DataState.Success(result)
        }
    }

    fun getArticleCreatedDate(articleId: Int) {
        viewModelScope.launch {
            val result = try {
                articleRepository.getArticleCreatedDate(articleId)
            } catch (e: Exception) {
                _createdDate.value = DataState.Error(e)
                return@launch
            }
            _createdDate.value = DataState.Success(result)
        }
    }

    fun setTerm(startDate: String, endDate: String) {
        _term.value = Pair(startDate, endDate)
    }

}