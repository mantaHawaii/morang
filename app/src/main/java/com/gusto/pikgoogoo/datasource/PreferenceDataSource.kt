package com.gusto.pikgoogoo.datasource

import android.content.Context

interface PreferenceDataSource {
    fun getUid(context: Context): Int
}