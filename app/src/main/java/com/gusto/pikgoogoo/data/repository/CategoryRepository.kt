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