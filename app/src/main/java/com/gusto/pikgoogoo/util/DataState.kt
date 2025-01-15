package com.gusto.pikgoogoo.util

import java.lang.Exception

sealed class DataState<out R> {
    data class Success<T>(val result:T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
    data class Loading(val string: String? = null): DataState<Nothing>()
}
