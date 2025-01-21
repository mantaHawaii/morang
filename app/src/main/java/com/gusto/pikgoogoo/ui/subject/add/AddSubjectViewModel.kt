package com.gusto.pikgoogoo.ui.subject.add

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
class AddSubjectViewModel
@Inject
constructor(
    private val subjectRepository: SubjectRepository
): ViewModel() {

    private val _categoriesData: MutableLiveData<DataState<List<Category>>> = MutableLiveData()
    private val _responseData: MutableLiveData<DataState<Pair<String, Int>>> = MutableLiveData()

    val categoriesData: LiveData<DataState<List<Category>>>
        get() = _categoriesData
    val responseData: LiveData<DataState<Pair<String, Int>>>
        get() = _responseData

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        subjectRepository.fetchCategoriesFlow().onEach { dataState ->
            _categoriesData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun insertSubject(title: String, categoryId: Int) {
        subjectRepository.insertSubjectFlow(title, categoryId).onEach { dataState ->
            _responseData.value = dataState
        }.launchIn(viewModelScope)
    }
    
}