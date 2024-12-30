package com.gusto.pikgoogoo

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.ui.alert.NetworkingAlertDialog
import com.gusto.pikgoogoo.util.ConnectionLiveData
import com.gusto.pikgoogoo.util.LoginManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.native_app_key))
    }

}