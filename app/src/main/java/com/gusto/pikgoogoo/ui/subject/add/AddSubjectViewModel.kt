package com.gusto.pikgoogoo.ui.subject.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Category
import com.gusto.pikgoogoo.data.repository.AuthModel
import com.gusto.pikgoogoo.data.repository.CategoryRepository
import com.gusto.pikgoogoo.data.repository.SubjectRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSubjectViewModel
@Inject
constructor(
    private val categoryRepository: CategoryRepository,
    private val subjectRepository: SubjectRepository,
    private val authModel: AuthModel
): ViewModel() {

    private val _categoriesData: MutableLiveData<DataState<List<Category>>> = MutableLiveData()
    private val _responseData: MutableLiveData<DataState<Pair<String, Int>>> = MutableLiveData()

    val categoriesData: LiveData<DataState<List<Category>>>
        get() = _categoriesData
    val responseData: LiveData<DataState<Pair<String, Int>>>
        get() = _responseData

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            val categories = try {
                categoryRepository.getCategories()
            } catch (e: Exception) {
                _categoriesData.value = DataState.Error(e)
                return@launch
            }
            _categoriesData.value = DataState.Success(categories)
        }
    }

    fun insertSubject(title: String, categoryId: Int) {
        viewModelScope.launch {
            val idToken = try {
                authModel.getIdToken()
            } catch (e: Exception) {
                _responseData.value = DataState.Error(e)
                return@launch
            }
            val result = try {
                subjectRepository.addSubject(title, categoryId, idToken)
            } catch (e: Exception) {
                _responseData.value = DataState.Error(e)
                return@launch
            }
            _responseData.value = DataState.Success(result)
        }
    }
    
}