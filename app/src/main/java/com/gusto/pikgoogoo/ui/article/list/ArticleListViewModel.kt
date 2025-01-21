package com.gusto.pikgoogoo.ui.article.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.gusto.pikgoogoo.data.Article
import com.gusto.pikgoogoo.data.repository.AdRepository
import com.gusto.pikgoogoo.data.repository.ArticleRepository
import com.gusto.pikgoogoo.data.repository.SubjectRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel
@Inject
constructor(
    private val articleRepository: ArticleRepository,
    private val subjectRepository: SubjectRepository,
    private val adRepository: AdRepository
) : ViewModel() {

    private val _articlesData: MutableLiveData<DataState<List<Article>>> = MutableLiveData()
    private val _adsData: MutableLiveData<DataState<NativeAd>> = MutableLiveData()
    private val _bookmarkRes: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _voteRes: MutableLiveData<DataState<Pair<String, Int>>> = MutableLiveData()
    private val _bookmarkData: MutableLiveData<DataState<Boolean>> = MutableLiveData()

    val articlesData: LiveData<DataState<List<Article>>>
        get() = _articlesData
    val adsData: LiveData<DataState<NativeAd>>
        get() = _adsData
    val bookmarkRes: LiveData<DataState<String>>
        get() = _bookmarkRes
    val voteRes: LiveData<DataState<Pair<String, Int>>>
        get() = _voteRes
    val bookmarkData: LiveData<DataState<Boolean>>
        get() = _bookmarkData

    val params: ArticleParameter =
        ArticleParameter(
            0,
            "",
            0,
            0
        )

    fun fetchArticles() {
        articleRepository.fetchArticlesFlow(params.subjectId, params.order, params.offset, params.searchWords)
            .onEach { dataState ->
                _articlesData.value = dataState
            }.launchIn(viewModelScope)
    }

    fun fetchAds(context: Context, numberOfAds: Int) {
        adRepository.fetchAds(context, numberOfAds).onEach { dataState ->
            _adsData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun voteArticleSubmit(articleId: Int) {
        articleRepository.voteArticleFlow(articleId, params.order).onEach { dataState ->
            _articlesData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun fetchBookmarkStatus(context: Context) {
        subjectRepository.fetchBookmarkStatusFlow(context, params.subjectId).onEach { dataState ->
            _bookmarkData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun addToBookmarks() {
        subjectRepository.bookmarkSubjectFlow(params.subjectId).onEach { dataState ->
            _bookmarkRes.value = dataState
        }.launchIn(viewModelScope)
    }

    data class ArticleParameter(
        var subjectId: Int,
        var searchWords: String,
        var offset: Int,
        var order: Int
    )

}