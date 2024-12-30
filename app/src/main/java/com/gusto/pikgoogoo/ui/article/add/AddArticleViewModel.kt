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

    fun insertArticle() {
        viewModelScope.launch {
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _insertState.value = DataState.Error(e)
                return@launch
            }
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

    fun storeImage(uri: Uri, context: Context) {

        val uid = try {
            userRepository.getUidFromShareRef(context)
        } catch (e: Exception) {
            _storageState.value = DataState.Error(e)
            return
        }

        val fileName = generateFileName(uid)

        viewModelScope.launch {
            val result = try {
                firebaseImageRepository.storeImage(uri, fileName)
            } catch (e: Exception) {
                _storageState.value = DataState.Error(e)
                return@launch
            }
            _storageState.value = DataState.Success(result)
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