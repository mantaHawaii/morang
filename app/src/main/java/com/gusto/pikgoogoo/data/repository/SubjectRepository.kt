package com.gusto.pikgoogoo.data.repository

import android.content.Context
import com.gusto.pikgoogoo.api.WebService
import com.gusto.pikgoogoo.data.CategoryMapper
import com.gusto.pikgoogoo.data.Response
import com.gusto.pikgoogoo.data.Subject
import com.gusto.pikgoogoo.data.SubjectMapper
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.datasource.PreferenceDataSource
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepository
@Inject
constructor(
    private val webService: WebService,
    private val subjectMapper: SubjectMapper,
    private val categoryMapper: CategoryMapper,
    private val firebaseDataSource: FirebaseDataSource,
    private val preferenceDataSource: PreferenceDataSource
) : ParentRepository() {

    val subjects = mutableListOf<Subject>()

    fun insertSubjectFlow(title: String, categoryId: Int) = flow {
        try {
            emit(DataState.Loading())
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.addSubject(idToken, title, categoryId)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(Pair(res.status.message, res.id)))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchSubjectsFlow(categoryId: Int, order: Int, offset: Int) = flow {
        try {
            val res = webService.getSubjects(categoryId, order, offset)
            if (isStatusCodeSuccess(res)) {
                if (offset == 0) {
                    subjects.clear()
                }
                val data = subjectMapper.mapFromEntityList(res.subjects)
                if (data.isNotEmpty()) {
                    subjects.addAll(data)
                }
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchSubjectsBySearchWordsFlow(categoryId: Int, order: Int, offset: Int, searchWords: String) = flow {
        try {
            val res = webService.getSubjectsBySearchWords(categoryId, order, offset, searchWords)
            if (isStatusCodeSuccess(res)) {
                if (offset == 0) {
                    subjects.clear()
                }
                val data = subjectMapper.mapFromEntityList(res.subjects)
                if (data.isNotEmpty()) {
                    subjects.addAll(data)
                }
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchCategoriesFlow() = flow {
        try {
            emit(DataState.Loading("카테고리 가져오는 중"))
            val res = webService.getCategories()
            if (isStatusCodeSuccess(res)) {
                val categories = categoryMapper.mapFromEntityList(res.categories)
                emit(DataState.Success(categories))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchBookmarkStatusFlow(context: Context, subjectId: Int) = flow {
        try {
            emit(DataState.Loading("북마크 상태 가져오는 중"))
            val uid = preferenceDataSource.getUid(context)
            val res = webService.isBookmarked(subjectId, uid)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(true))
            } else if (res.status.code == "011") {
                emit(DataState.Success(false))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun bookmarkSubjectFlow(subjectId: Int) = flow {
        try {
            emit(DataState.Loading("북마크 요청 중"))
            val firebaseUser = firebaseDataSource.getCurrentUser()
            val idToken = firebaseDataSource.getIDTokenByUser(firebaseUser)
            val res = webService.bookmarkSubject(subjectId, idToken)
            if (isStatusCodeSuccess(res)) {
                emit(DataState.Success(res.status.message))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchBookmarkedSubjectsFlow(context: Context, order: Int, offset: Int) = flow {
        try {
            emit(DataState.Loading("항목 정보 가져오는 중"))
            val uid = preferenceDataSource.getUid(context)
            val res = webService.getBookmarkedSubjects(uid, order, offset)
            if (isStatusCodeSuccess(res)) {
                if (offset == 0) {
                    subjects.clear()
                }
                val data = subjectMapper.mapFromEntityList(res.subjects)
                if (data.isNotEmpty()) {
                    subjects.addAll(data)
                }
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchMySubjectsFlow(context: Context, order: Int, offset: Int) = flow {
        try {
            emit(DataState.Loading("항목 정보 가져오는 중"))
            val uid = preferenceDataSource.getUid(context)
            val res = webService.getMySubjects(uid, order, offset)
            if (isStatusCodeSuccess(res)) {
                if (offset == 0) {
                    subjects.clear()
                }
                val data = subjectMapper.mapFromEntityList(res.subjects)
                if (data.isNotEmpty()) {
                    subjects.addAll(data)
                }
                emit(DataState.Success(subjects))
            } else {
                emit(DataState.Error(formatErrorFromStatus(res)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}