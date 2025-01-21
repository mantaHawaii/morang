package com.gusto.pikgoogoo.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.data.Category
import com.gusto.pikgoogoo.data.repository.SubjectRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel
@Inject
constructor(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _categoriesData: MutableLiveData<DataState<List<Category>>> = MutableLiveData()

    val categoriesData: LiveData<DataState<List<Category>>>
        get() = _categoriesData

    fun fetchCategories() {
        subjectRepository.fetchCategoriesFlow().onEach { dataState ->
            _categoriesData.value = dataState
        }.launchIn(viewModelScope)
    }

}