package com.gusto.pikgoogoo.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogLoadingBinding
    private var string: String = "로딩 중입니다"

    override fun onStart() {
        super.onStart()
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_loading, null, false)

        setContentView(binding.root)

        binding.tvText.text = string

    }

    fun setText(str: String) {
        if (this::binding.isInitialized) {
            string = str
            binding.tvText.text = str
        } else {
            string = str
        }
    }

}