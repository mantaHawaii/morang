package com.gusto.pikgoogoo.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.ActivityMainBinding
import com.gusto.pikgoogoo.ui.alert.CustomAlertDialog
import com.gusto.pikgoogoo.ui.alert.NetworkingAlertDialog
import com.gusto.pikgoogoo.ui.splash.SplashActivity
import com.gusto.pikgoogoo.ui.article.list.ArticleListFragment
import com.gusto.pikgoogoo.ui.login.LoginActivity
import com.gusto.pikgoogoo.ui.myinfo.MyInfoFragment
import com.gusto.pikgoogoo.ui.profile.edit.EditProfileFragment
import com.gusto.pikgoogoo.ui.subject.list.SubjectListFragment
import com.gusto.pikgoogoo.util.ConnectionLiveData
import com.gusto.pikgoogoo.util.MrBackPressedListener
import com.gusto.pikgoogoo.util.LoginManager
import com.gusto.pikgoogoo.util.MRActivity
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MRActivity() {

    @Inject
    lateinit var loginManager: LoginManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MR_MA", this.toString())

        /*if (Build.VERSION.SDK_INT >= 23) {
            window.decorView.systemUiVisibility =View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }*/
        if (!loginManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fcv_main, SubjectListFragment(), FragmentTags.SUBJECT_LIST_TAG)
            .commit()

    }

    override fun onResume() {
        super.onResume()

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    val subjectId = deepLink!!.getQueryParameter("subjectId")!!.toInt()
                    val title =
                        URLDecoder.decode(deepLink!!.getQueryParameter("title"), "UTF-8")

                    showFragmentOnFullScreen(
                        ArticleListFragment(subjectId, title),
                        FragmentTags.ARTICLE_LIST_TAG,
                        false
                    )

                }
            }

    }

    fun alertLogin() {
        val map = mutableMapOf<String, String>()
        map.put("LEFT", "로그인")
        map.put("RIGHT", "닫기")
        map.put("CONTENT", "해당 기능을 사용하기 위해서는 로그인이 필요합니다")
        val dialog = CustomAlertDialog(this, { any ->
            logOut()
        }, map)
        dialog.show()
    }

    fun logOut() {
        loginManager.logOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun showFragmentOnFullScreen(fragmentClass: Fragment, tag: String, withAnimation: Boolean = true) {

        val view = binding.fcvMain

        if (withAnimation) {
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                addUpdateListener { updatedAnimation ->
                    view.alpha = updatedAnimation.animatedValue as Float
                }
                start()
            }
        }

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fcv_main, fragmentClass, tag)
            .addToBackStack(tag)
            .commit()

    }

    fun closeFullScreen() {

        val view = binding.fcvMain
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 200
            addUpdateListener { updatedAnimation ->
                view.alpha = updatedAnimation.animatedValue as Float
            }
            start()

        }.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                supportFragmentManager.popBackStackImmediate()
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 200
                    addUpdateListener { updatedAnimation ->
                        view.alpha = updatedAnimation.animatedValue as Float
                    }
                    start()

                }
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                supportFragmentManager.popBackStackImmediate()
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 200
                    addUpdateListener { updatedAnimation ->
                        view.alpha = updatedAnimation.animatedValue as Float
                    }
                    start()

                }
            }
        })

    }

    fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}