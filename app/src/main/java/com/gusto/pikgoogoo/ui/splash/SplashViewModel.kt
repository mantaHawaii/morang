package com.gusto.pikgoogoo.ui.splash

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.repository.CacheRepository
import com.gusto.pikgoogoo.data.repository.GradeRepository
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
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
        viewModelScope.launch {
            val result = try {
                gradeRepository.getServerDBVersion()
            } catch (e: Exception) {
                _dbVersionData.value = DataState.Error(e)
                return@launch
            }
            _dbVersionData.value = DataState.Success(result)
        }
    }

    fun getDBVersionFromPref(activity: SplashActivity): Int {
        val pref = activity.getPreferences(Context.MODE_PRIVATE)
        return pref.getInt(activity.getString(R.string.preference_local_db_version_key), 0)
    }

    fun setDBVersionOfPerf(activity: SplashActivity, dbVersion: Int) {
        activity.getPreferences(Context.MODE_PRIVATE)
            .edit()
            .putInt(activity.getString(R.string.preference_local_db_version_key), dbVersion)
            .apply()
    }

    fun updateLocalGradeTable() {
        viewModelScope.launch {
            val result = try {
                gradeRepository.insertGradeFromServer()
            } catch (e: Exception) {
                _gradeDataState.value = DataState.Error(e)
                return@launch
            }
            _gradeDataState.value = DataState.Success(result)
        }
    }

    fun deleteCache(context: Context) {
        try {
            cacheRepository.deleteCashe(context)
        } catch (e: Exception) {
            return
        }
    }

}