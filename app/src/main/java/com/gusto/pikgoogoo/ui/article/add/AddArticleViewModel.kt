package com.gusto.pikgoogoo.ui.article.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.repository.ArticleRepository
import com.gusto.pikgoogoo.data.repository.FirebaseImageRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddArticleViewModel
@Inject
constructor(
    private val articleRepository: ArticleRepository,
    private val firebaseImageRepository: FirebaseImageRepository
): ViewModel() {

    private val _insertState: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _storageState: MutableLiveData<DataState<String>> = MutableLiveData()

    val insertState: LiveData<DataState<String>>
        get() = _insertState
    val storageState: LiveData<DataState<String>>
        get() = _storageState

    val params = AddArticleReqParam("", 0, "", 0)

    fun submitArticleInsert() {
        viewModelScope.launch {
            articleRepository.insertArticleFlow(
                params.content,
                params.subjectId,
                params.imageUrl.replace(".jpg", ""),
                params.cropImage)
                .onEach { dataState ->
                    _insertState.value = dataState
                }.launchIn(viewModelScope)
        }
    }

    fun submitImageStore(uri: Uri, context: Context) {
        viewModelScope.launch {
            firebaseImageRepository.storeImageFlow(uri, context).onEach { dataState ->
                _storageState.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    data class AddArticleReqParam(
        var content: String,
        var subjectId: Int,
        var imageUrl: String,
        var cropImage: Int
    )

}