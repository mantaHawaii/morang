package com.gusto.pikgoogoo.ui.login

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.LoginCode
import com.gusto.pikgoogoo.databinding.ActivityLoginBinding
import com.gusto.pikgoogoo.ui.LoadingDialog
import com.gusto.pikgoogoo.ui.gp.GPDialog
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.util.LoginManager
import com.gusto.pikgoogoo.util.MRActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : MRActivity() {

    @Inject
    lateinit var loginManager: LoginManager
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: LoginViewModel by viewModels()
    private val TAG = "MR_LA"
    private lateinit var loadingDialog: LoadingDialog
    private var _MRClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        binding.ivLogoCenter.setOnClickListener {
            _MRClickCount++
            if (_MRClickCount >= 5) {
                _MRClickCount = 0
                val gpDialog = GPDialog(this) { uid ->
                    viewModel.getTokenIdGP(uid)
                }
                gpDialog.show()
            }
        }

        binding.buttonSignInGoogle.setOnClickListener {
            loginManager.logOut()
            viewModel.getGoogleIdToken(this)
        }

        binding.buttonSignInKakao.setOnClickListener {
            loginManager.logOut()
            viewModel.getKakaoToken(this)
        }

        binding.buttonGuest.setOnClickListener { view ->
            moveToMain(-1)
        }

        subscribeObservers()

    }

    fun subscribeObservers() {
        viewModel.userData.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    showLoadingDialog(dataState.string ?: "사용자 정보 가져오는 중")
                }
                is DataState.Success -> {
                    Log.d("MR_LA", "유저 정보 가져왔습니다: "+dataState.result.id.toString())
                    closeLoadingDialog()
                    loginManager.logIn()
                    putId(dataState.result.id)
                    moveToMain(1)
                }
                is DataState.Failure -> {

                    closeLoadingDialog()

                    if (dataState.string.startsWith("001", true)) {
                        showRegisterWindow()
                    } else {
                        Toast.makeText(this, dataState.string, Toast.LENGTH_SHORT).show()
                    }

                }
                is DataState.Error -> {
                    closeLoadingDialog()
                }
            }
        })
        viewModel.registerState.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    showLoadingDialog(dataState.string ?: "등록 정보 가져오는 중")
                }
                is DataState.Success -> {
                    Toast.makeText(this, dataState.result, Toast.LENGTH_LONG).show()
                    viewModel.getTokenIdAuthUser(false)
                    closeLoadingDialog()
                }
                is DataState.Failure -> {
                    Toast.makeText(this, dataState.string, Toast.LENGTH_LONG).show()
                    closeLoadingDialog()
                }
                is DataState.Error -> {
                    closeLoadingDialog()
                }
            }
        })
        viewModel.googleTokenState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    showLoadingDialog(dataState.string ?: "로딩 중")
                }
                is DataState.Error -> {
                    closeLoadingDialog()
                    Toast.makeText(this, dataState.exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
                is DataState.Failure -> {
                    closeLoadingDialog()
                    Toast.makeText(this, dataState.string, Toast.LENGTH_LONG).show()
                }
                is DataState.Success -> {
                    closeLoadingDialog()
                    viewModel.getTokenIdGoogle(dataState.result)
                }
            }
        })
        viewModel.kakaoTokenState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    showLoadingDialog(dataState.string ?: "로딩 중")
                }
                is DataState.Error -> {
                    closeLoadingDialog()
                    Toast.makeText(this, dataState.exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
                is DataState.Failure -> {
                    closeLoadingDialog()
                    Toast.makeText(this, dataState.string, Toast.LENGTH_LONG).show()
                }
                is DataState.Success -> {
                    Log.d("MR_LA", "카카오 토큰 정보 가져왔습니다")
                    closeLoadingDialog()
                    viewModel.getTokenIdKakao(dataState.result)
                }
            }
        })
        viewModel.idTokenState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    showLoadingDialog(dataState.string ?: "로딩 중")
                }
                is DataState.Error -> {
                    closeLoadingDialog()
                }
                is DataState.Failure -> {
                    closeLoadingDialog()
                }
                is DataState.Success -> {
                    Log.d("MR_LA", "ID 토큰 정보 가져왔습니다")
                    closeLoadingDialog()
                    val result = dataState.result
                    if (result.second == LoginCode.NOTHING) {
                        viewModel.getUserByToken()
                    }
                }
            }
        })
    }


    fun showRegisterWindow() {

        val view = binding.fragmentContainerView

        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                val heightF = view.height.toFloat()
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view.visibility = View.GONE

                ValueAnimator.ofFloat(heightF, 0f).apply {
                    duration = 250
                    addUpdateListener { updatedAnimation ->
                        Log.d(TAG, view.translationY.toString())
                        if (view.translationY >= heightF) {
                            view.visibility = View.VISIBLE
                        }
                        view.translationY = updatedAnimation.animatedValue as Float
                    }
                    start()
                }

            }

        })

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.fragment_container_view, RegisterFragment(), null)
            .commit()

        view.visibility = View.VISIBLE

    }

    fun moveToMain(loginState: Int) {
        Log.d("MR_LA", "메인으로 이동하겠습니다")
        val mIntent = Intent(this, MainActivity::class.java)
        mIntent.putExtra("LoginState", loginState)
        startActivity(mIntent)
        this.finish()
    }

    fun showLoadingDialog(string: String) {
        loadingDialog.setText(string)
        loadingDialog.show()
    }

    fun closeLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun putId(id: Int) {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(getString(R.string.preference_user_key), id).apply()
    }

}