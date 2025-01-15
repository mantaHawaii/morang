package com.gusto.pikgoogoo.ui.article.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Article
import com.gusto.pikgoogoo.data.repository.ArticleRepository
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.FirebaseImageRepository
import com.gusto.pikgoogoo.data.repository.SubjectRepository
import com.gusto.pikgoogoo.data.repository.UserRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel
@Inject
constructor(
    private val articleRepository: ArticleRepository,
    private val subjectRepository: SubjectRepository,
    private val authModel: AuthModel,
    private val userRepository: UserRepository,
    private val firebaseImageRepository: FirebaseImageRepository
) : ViewModel() {

    private val _articlesData: MutableLiveData<DataState<List<Article>>> = MutableLiveData()
    private val _bookmarkRes: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _voteRes: MutableLiveData<DataState<Pair<String, Int>>> = MutableLiveData()
    private val _totalVoteCountData: MutableLiveData<Int> = MutableLiveData()
    private val _isBookmarked: MutableLiveData<Boolean> = MutableLiveData()

    val articlesData: LiveData<DataState<List<Article>>>
        get() = _articlesData
    val bookmarkRes: LiveData<DataState<String>>
        get() = _bookmarkRes
    val voteRes: LiveData<DataState<Pair<String, Int>>>
        get() = _voteRes
    val totalVoteCountData: LiveData<Int>
        get() = _totalVoteCountData
    val isBookmarked: LiveData<Boolean>
        get() = _isBookmarked

    val params: ArticleParameter =
        ArticleParameter(
            0,
            "",
            0,
            0
        )
    private val articles = mutableListOf<Article>()
    var moreFlag = true

    fun fetchArticles() {
        viewModelScope.launch {

            if (params.offset == 0) {
                articles.clear()
            }

            _articlesData.value = DataState.Loading("데이터 가져오는 중")

            val data = try {
                articleRepository.fetchArticles(params.subjectId, params.order, params.offset, params.searchWords)
            } catch (e: Exception) {
                _articlesData.value = DataState.Error(e)
                return@launch
            }

            val voteCount = try {
                articleRepository.getVoteCount(params.subjectId, params.order)
            } catch (e: Exception) {
                _articlesData.value = DataState.Error(e)
                return@launch
            }

            _totalVoteCountData.value = voteCount

            for (item in data) {
                item.voteRate = if (voteCount == 0) 0.0f else (item.voteCount / voteCount) * 100.0f
                val thumbnailUri = try {
                    firebaseImageRepository.getThumbUri(item.imageUrl)
                } catch (e: Exception) {
                    _articlesData.value = DataState.Error(e)
                    return@launch
                }
                item.imageUri = thumbnailUri
            }

            moreFlag = data.size > 0

            articles.addAll(data)
            _articlesData.value = DataState.Success(articles)

        }
    }

    fun voteArticle(articleId: Int, pos: Int) {
        viewModelScope.launch {
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _voteRes.value = DataState.Error(e)
                return@launch
            }
            val msg = try {
                articleRepository.voteArticle(idToken, articleId)
            } catch (e: Exception) {
                _voteRes.value = DataState.Error(e)
                return@launch
            }
            _voteRes.value = DataState.Success(Pair(msg, pos))
        }
    }

    fun isBookmarked(context: Context) {
        viewModelScope.launch {
            val uid = try {
                userRepository.getUidFromShareRef(context)
            } catch (e: Exception) {
                _isBookmarked.value = false
                return@launch
            }
            _isBookmarked.value = subjectRepository.isBookmarked(params.subjectId, uid)
        }
    }

    fun bookmarkSubject() {
        viewModelScope.launch {
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _bookmarkRes.value = DataState.Error(e)
                return@launch
            }
            val msg = try {
                subjectRepository.bookmarkSubject(params.subjectId, idToken)
            } catch (e: Exception) {
                _bookmarkRes.value = DataState.Error(e)
                return@launch
            }
            _bookmarkRes.value = DataState.Success(msg)
        }
    }

    data class ArticleParameter(
        var subjectId: Int,
        var searchWords: String,
        var offset: Int,
        var order: Int
    )

}