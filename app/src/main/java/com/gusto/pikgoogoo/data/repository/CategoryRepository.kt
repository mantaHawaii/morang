package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.Category
import com.gusto.pikgoogoo.data.CategoryMapper
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepository
@Inject
constructor(
    private val webService: WebService,
    private val categoryMapper: CategoryMapper
){

    suspend fun getCategoriesFlow() : Flow<DataState<List<Category>>> = flow {
        emit(DataState.Loading())
        try {

            val res = webService.getCategories()
            val code = res.status.code

            if (code.equals("111")) {
                val categories = categoryMapper.mapFromEntityList(res.categories)
                emit(DataState.Success(categories))
            } else {
                emit(DataState.Failure(res.status.message))
            }

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getCategories() : List<Category> {
        val res = withContext(Dispatchers.IO) {
            webService.getCategories()
        }
        if (res.status.code == "111") {
            return categoryMapper.mapFromEntityList(res.categories)
        } else {
            throw Exception(res.status.message)
        }
    }

}