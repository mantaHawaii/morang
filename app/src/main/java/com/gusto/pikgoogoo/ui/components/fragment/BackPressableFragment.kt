package com.gusto.pikgoogoo.ui.components.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

open class BackPressableFragment : Fragment() {

    var enableToBack = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200
            addUpdateListener { updatedAnimation ->
                view.alpha = updatedAnimation.animatedValue as Float
            }
            start()
        }
    }

    open fun backPressed(exitStyle: FragmentExitStyle = FragmentExitStyle.FADE_OUT) {
        Log.d("MR_BPF", "backpressed")
        if (enableToBack) {
            val rootView = requireView()
            when(exitStyle) {
                FragmentExitStyle.SLIDE_DOWN -> {
                    var f = rootView.measuredHeight.toFloat()
                    ValueAnimator.ofFloat(0f, f).apply {
                        duration = 150
                        addUpdateListener { updatedAnimation ->
                            rootView.translationY = updatedAnimation.animatedValue as Float
                            if (rootView.translationY == f) {
                                Log.d("MR_BPF", parentFragmentManager.backStackEntryCount.toString())
                                parentFragmentManager.popBackStackImmediate()
                            }
                        }
                        start()
                    }
                }
                FragmentExitStyle.SLIDE_AWAY -> {
                    var f = rootView.measuredWidth.toFloat()
                    ValueAnimator.ofFloat(0f, f).apply {
                        duration = 150
                        addUpdateListener { updatedAnimation ->
                            rootView.translationX = updatedAnimation.animatedValue as Float
                            if (rootView.translationX == f) {
                                Log.d("MR_BPF", parentFragmentManager.backStackEntryCount.toString())
                                parentFragmentManager.popBackStackImmediate()
                            }
                        }
                        start()
                    }
                }
                FragmentExitStyle.FADE_OUT -> {
                    ValueAnimator.ofFloat(1f, 0f).apply {
                        duration = 150
                        addUpdateListener { updatedAnimation ->
                            rootView.alpha = updatedAnimation.animatedValue as Float
                            if (rootView.alpha == 0f) {
                                Log.d("MR_BPF", parentFragmentManager.backStackEntryCount.toString())
                                parentFragmentManager.popBackStackImmediate()
                            }
                        }
                        start()
                    }
                }
                FragmentExitStyle.NONE -> {
                    parentFragmentManager.popBackStackImmediate()
                }
            }
        }
    }

    enum class FragmentExitStyle {
        FADE_OUT,
        SLIDE_AWAY,
        SLIDE_DOWN,
        NONE
    }

}