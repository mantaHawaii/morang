package com.gusto.pikgoogoo.ui.components.fragment

import android.os.Bundle
import android.widget.Toast
import com.gusto.pikgoogoo.ui.LoadingDialog

open class LoadingIndicatorFragment: BackPressableFragment() {
    var isLoading = false
    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
    }

    open fun loadStart(msg: String?) {
        isLoading = true
        loadingDialog.setText(msg?:"로딩 중")
        loadingDialog.show()
    }
    open fun loadEnd() {
        isLoading = false
        loadingDialog.dismiss()
    }

    open fun showMessage(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
    }
}