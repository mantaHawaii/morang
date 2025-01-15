package com.gusto.pikgoogoo.ui.login

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
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
import com.gusto.pikgoogoo.data.tag.FragmentTags
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
    private val viewModel: LoginViewModel by viewModels()
    private val TAG = "MR_LA"
    private lateinit var loadingDialog: LoadingDialog
    private var _MRClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.ivLogoCenter.setOnClickListener {
            _MRClickCount++
            if (_MRClickCount >= 5) {
                _MRClickCount = 0
                val gpDialog = GPDialog(this) { uid ->
                    viewModel.getIdTokenWithGP(uid)
                }
                gpDialog.show()
            }
        }

        binding.buttonSignInGoogle.setOnClickListener {
            loginManager.logOut()
            viewModel.getMorangUserWithGoogleSignIn(this)
        }

        binding.buttonSignInKakao.setOnClickListener {
            loginManager.logOut()
            viewModel.getIdTokenWithKakaoSignIn(this)
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
                    Log.d(TAG, "유저 정보 가져왔습니다: "+dataState.result.id.toString())
                    closeLoadingDialog()
                    val uid = dataState.result.id
                    if (uid <= 0) {
                        showRegisterWindow()
                    } else {
                        loginManager.logIn(dataState.result.id)
                        moveToMain(1)
                    }
                }
                is DataState.Error -> {
                    closeLoadingDialog()
                    Toast.makeText(this, dataState.exception.localizedMessage?:"에러", Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.registerState.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    showLoadingDialog(dataState.string ?: "등록 정보 가져오는 중")
                }
                is DataState.Success -> {
                    closeLoadingDialog()
                    loginManager.logIn(dataState.result)
                    moveToMain(1)
                }
                is DataState.Error -> {
                    closeLoadingDialog()
                    Toast.makeText(this, dataState.exception.localizedMessage?:"에러", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun showRegisterWindow() {

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
            .add(R.id.fragment_container_view, RegisterFragment(), FragmentTags.REGISTER_TAG)
            .commit()

        view.visibility = View.VISIBLE

    }

    fun moveToMain(loginState: Int) {
        Log.d(TAG, "메인으로 이동하겠습니다")
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

}