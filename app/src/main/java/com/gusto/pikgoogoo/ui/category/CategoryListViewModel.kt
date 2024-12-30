package com.gusto.pikgoogoo.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Category
import com.gusto.pikgoogoo.data.repository.CategoryRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel
@Inject
constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoriesData: MutableLiveData<DataState<List<Category>>> = MutableLiveData()

    val categoriesData: LiveData<DataState<List<Category>>>
        get() = _categoriesData

    fun getCategories() {
        viewModelScope.launch {
            val result = try {
                categoryRepository.getCategories()
            } catch (e: Exception) {
                _categoriesData.value = DataState.Error(e)
                return@launch
            }
            _categoriesData.value = DataState.Success(result)
        }
    }

}