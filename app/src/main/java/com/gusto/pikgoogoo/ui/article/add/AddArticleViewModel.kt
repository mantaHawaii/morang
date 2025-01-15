package com.gusto.pikgoogoo.ui.article.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.repository.ArticleRepository
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.FirebaseImageRepository
import com.gusto.pikgoogoo.data.repository.UserRepository
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
    private val userRepository: UserRepository,
    private val firebaseImageRepository: FirebaseImageRepository,
    private val authModel: AuthModel
): ViewModel() {

    private val _insertState: MutableLiveData<DataState<String>> = MutableLiveData()
    private val _storageState: MutableLiveData<DataState<String>> = MutableLiveData()

    val insertState: LiveData<DataState<String>>
        get() = _insertState
    val storageState: LiveData<DataState<String>>
        get() = _storageState

    val params = AddArticleReqParam("", 0, "", 0)

    @Deprecated("2025-01-15 이후로 사용되지 않는 함수")
    fun insertArticle() {
        viewModelScope.launch {

            _insertState.value = DataState.Loading("ID 토큰 가져오는 중")
            val idToken = try {
                authModel.getIdTokenByUser()
            } catch (e: Exception) {
                _insertState.value = DataState.Error(e)
                return@launch
            }

            _insertState.value = DataState.Loading("서버에 항목 쓰기 요청 중")
            val result = try {
                articleRepository.insertArticle(
                    idToken,
                    params.content,
                    params.subjectId,
                    params.imageUrl.replace(".jpg", ""),
                    params.cropImage
                )
            } catch (e: Exception) {
                _insertState.value = DataState.Error(e)
                return@launch
            }
            _insertState.value = DataState.Success(result)
        }
    }

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

    //삭제 완료
    @Deprecated("2025-01-15 이후로 사용되지 않는 함수")
    fun storeImage(uri: Uri, context: Context) {

        _storageState.value = DataState.Loading("유저 아이디 가져오는 중")
        val uid = try {
            userRepository.getUidFromShareRef(context)
        } catch (e: Exception) {
            _storageState.value = DataState.Error(e)
            return
        }

        val fileName = generateFileName(uid)

        viewModelScope.launch {
            _storageState.value = DataState.Loading("파이어베이스에 이미지 저장 중")
            val result = try {
                firebaseImageRepository.storeImage(uri, fileName)
            } catch (e: Exception) {
                _storageState.value = DataState.Error(e)
                return@launch
            }
            _storageState.value = DataState.Success(result)
        }

    }

    fun submitImageStore(uri: Uri, context: Context) {
        viewModelScope.launch {
            firebaseImageRepository.storeImageFlow(uri, context).onEach { dataState ->
                _storageState.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    private fun generateFileName(uid: Int): String {
        if (uid > 0) {
            return uid.toString() + "_" + System.currentTimeMillis().toString()
        } else {
            throw Exception("잘못된 형식의 UID입니다")
        }
    }

    data class AddArticleReqParam(
        var content: String,
        var subjectId: Int,
        var imageUrl: String,
        var cropImage: Int
    )

}