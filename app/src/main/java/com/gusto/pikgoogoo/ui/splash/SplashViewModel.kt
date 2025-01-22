package com.gusto.pikgoogoo.ui.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.repository.CacheRepository
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject
constructor(
    private val gradeRepository: GradeRepository,
    private val cacheRepository: CacheRepository
) : ViewModel() {

    private val _dbVersionData: MutableLiveData<DataState<Int>> = MutableLiveData()
    private val _gradeDataState: MutableLiveData<DataState<String>> = MutableLiveData()

    val dbVersionData: LiveData<DataState<Int>>
        get() = _dbVersionData
    val gradeDataState: LiveData<DataState<String>>
        get() = _gradeDataState

    fun getDBVersionFromServer() {
        gradeRepository.getServerDBVersionFlow().onEach { dataState ->
            _dbVersionData.value = dataState
        }.launchIn(viewModelScope)
    }

    fun getDBVersionFromPref(activity: SplashActivity): Int {
        val pref = activity.getPreferences(Context.MODE_PRIVATE)
        return pref.getInt(activity.getString(R.string.preference_local_db_version_key), 0)
    }

    fun setDBVersionOfPerf(activity: SplashActivity, dbVersion: Int) {
        Log.d("MR_SVM", "setDBVersionOfPerf")
        activity.getPreferences(Context.MODE_PRIVATE)
            .edit()
            .putInt(activity.getString(R.string.preference_local_db_version_key), dbVersion)
            .apply()
    }

    fun updateLocalGrade() {
        gradeRepository.updateLocalUserGradeFlow().onEach { dataState ->
            _gradeDataState.value = dataState
        }.launchIn(viewModelScope)
    }

    fun deleteCache(context: Context) {
        try {
            cacheRepository.deleteCashe(context)
        } catch (e: Exception) {
            return
        }
    }

}