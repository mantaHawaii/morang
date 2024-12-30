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
                        viewModel.updateLocalGradeTable()
                        viewModel.deleteCache(this)
                        Log.d(TAG, "2")
                    } else {
                        Log.d(TAG, "3")
                        getStarted()
                    }
                }
                is DataState.Failure -> {
                    Log.d(TAG, "5")
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
                    viewModel.setDBVersionOfPerf(this, serverDBVersion)
                    getStarted()
                }
                is DataState.Failure -> {
                    Log.d(TAG, "10")
                }
                is DataState.Error -> {
                    Log.d(TAG, "11: "+dataState.exception.toString())
                }
            }
        })
    }

    private fun getStarted() {
        var intent: Intent? = null

        if (loginManager.isLoggedIn()) {
            intent = Intent(this, MainActivity::class.java)
        } else {
            intent = Intent(this, LoginActivity::class.java)
        }

        if (!isStarted){
            startActivity(intent)
            isStarted = true
        }
        finish()
    }

}