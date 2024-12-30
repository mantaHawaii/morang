package com.gusto.pikgoogoo.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.R
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LoginManager
@Inject
constructor(
    private val context: Context
) {

    private val key = "isLoggedIn"
    private lateinit var sharedPref: SharedPreferences


    init {
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }

    fun isLoggedIn(): Boolean {

        val firebaseAuth = Firebase.auth
        var isLoggedInFirebase = false

        if (firebaseAuth.currentUser != null) {
            Log.d("99_", "not null")
            isLoggedInFirebase = true
        } else {
            Log.d("99_", "null")
            isLoggedInFirebase = false
        }
        return sharedPref.getBoolean(key, false) && isLoggedInFirebase
    }

    fun logIn() {
        val editor = sharedPref.edit()
        editor.putBoolean(key, true).apply()
    }

    fun logOut() {
        // 파이어베이스 로그아웃
        Firebase.auth.signOut()
        // 카카오 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
            }
            else {
            }
        }

        val editor = sharedPref.edit()
        editor.putInt(context.getString(R.string.preference_user_key), 0)
        editor.putBoolean(key, false).apply()
    }

}