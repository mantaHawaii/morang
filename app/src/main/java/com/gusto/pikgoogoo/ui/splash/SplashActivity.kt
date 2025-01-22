package com.gusto.pikgoogoo.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.window.SplashScreen
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.ui.login.LoginActivity
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.util.LoginManager
import com.gusto.pikgoogoo.util.MRActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : MRActivity() {

    @Inject
    lateinit var loginManager: LoginManager

    private var serverDBVersion: Int = 0
    var isStarted = false
    private val TAG = "MR_SA"

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            true
        }

        subscribeObservers()

        Log.d(TAG, "SPLASH_onCreate")
        Log.d(TAG, loginManager.toString())

    }

    override fun onNetWorkConnected() {
        super.onNetWorkConnected()
        viewModel.getDBVersionFromServer()
    }

    fun subscribeObservers() {
        viewModel.dbVersionData.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    Log.d(TAG, "1")
                }
                is DataState.Success -> {
                    val currentVersion = viewModel.getDBVersionFromPref(this)
                    if (dataState.result > currentVersion) {
                        serverDBVersion = dataState.result
                        viewModel.updateLocalGrade()
                        viewModel.deleteCache(this)
                        Log.d(TAG, "2")
                    } else {
                        Log.d(TAG, "3")
                        getStarted()
                    }
                }
                is DataState.Error -> {
                    Log.d(TAG, "6"+dataState.exception.toString())
                }
            }
        })
        viewModel.gradeDataState.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    Log.d(TAG, "7")
                }
                is DataState.Success -> {
                    Log.d(TAG, "DataState.Success")
                    viewModel.setDBVersionOfPerf(this, serverDBVersion)
                    getStarted()
                }
                is DataState.Error -> {
                    Log.d(TAG, "11: "+dataState.exception.toString())
                }
            }
        })
    }

    private fun getStarted() {
        var intent: Intent? = null
        Log.d(TAG, "getStarted()")
        if (loginManager.isLoggedIn()) {
            Log.d(TAG, "getStarted()->"+"LoggedIn")
            intent = Intent(this, MainActivity::class.java)
        } else {
            Log.d(TAG, "getStarted()->"+"Not LoggedIn")
            intent = Intent(this, LoginActivity::class.java)
        }

        if (!isStarted){
            Log.d(TAG, "getStarted()->"+"Start Activity!")
            startActivity(intent)
            isStarted = true
        }
        finish()
    }

}