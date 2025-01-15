package com.gusto.pikgoogoo.datasource

import android.content.Context
import com.gusto.pikgoogoo.R

class PreferenceDataSourceImpl: PreferenceDataSource {

    override fun getUid(context: Context): Int {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val uid = sharedPref.getInt(context.getString(R.string.preference_user_key), 0)
        return uid
    }

}