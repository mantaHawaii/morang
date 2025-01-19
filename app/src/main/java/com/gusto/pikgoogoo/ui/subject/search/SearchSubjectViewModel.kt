package com.gusto.pikgoogoo.ui.subject.search

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.gusto.pikgoogoo.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchSubjectViewModel
@Inject
constructor() : ViewModel() {

    private val gson = Gson()
    private lateinit var sharedPref: SharedPreferences
    private val key = "SEARCH_WORDS"

    fun saveSearchHistory(context: Context, words: String) {
        val editor = try {
            getEditor(context)
        } catch (e: Exception) {
            return
        }
        val list = try {
            getSearchHistory(context)
        } catch (e: Exception) {
            return
        }
        val searchWords = words.trim()
        val sameWordsIndex = list.indexOfFirst { it.trim() == searchWords }
        if (sameWordsIndex >= 0) {
            list.removeAt(sameWordsIndex)
        }
        if (searchWords.length > 0) {
            list.add(0, searchWords)
        }
        editor.putString(key, gson.toJson(list))
        editor.apply()
    }

    fun getSearchHistory(context: Context): ArrayList<String> {
        if (!::sharedPref.isInitialized) {
            sharedPref = context.getSharedPreferences(context.getString(R.string.preference_searchwords_key), Context.MODE_PRIVATE)
        }
        val string: String = sharedPref.getString(key, "[]")!!
        return gson.fromJson(string, ArrayList<String>().javaClass)
    }

    fun removeFromSearchHistory(context: Context, pos: Int) {
        val editor = try {
            getEditor(context)
        } catch (e: Exception) {
            return
        }
        val list = try {
            getSearchHistory(context)
        } catch (e: Exception) {
            return
        }
        list.removeAt(pos)
        editor.putString(key, gson.toJson(list))
        editor.apply()
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        if (!::sharedPref.isInitialized) {
            sharedPref = context.getSharedPreferences(context.getString(R.string.preference_searchwords_key), Context.MODE_PRIVATE)
        }
        return sharedPref.edit()?: throw Exception("데이터를 불러오는 데 실패했습니다. 앱을 재시작 후 다시 시도해 주세요")
    }

}