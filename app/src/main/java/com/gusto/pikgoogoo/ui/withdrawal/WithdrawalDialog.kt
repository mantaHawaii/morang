package com.gusto.pikgoogoo.ui.withdrawal

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.databinding.DialogWitdrawalBinding
import com.gusto.pikgoogoo.ui.LoadingDialog
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawalDialog: DialogFragment() {

    private lateinit var binding: DialogWitdrawalBinding
    private val viewModel: WithdrawalViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog

    override fun onStart() {
        dialog!!.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogWitdrawalBinding.inflate(inflater, container, false)
        val v = binding.root

        loadingDialog = LoadingDialog(requireActivity())

        binding.bCancel.setOnClickListener {
            dismiss()
        }
        binding.bOkay.setOnClickListener {
            viewModel.requestWithdrawal()
        }

        subscribeObservers()

        return v
    }

    private fun subscribeObservers() {
        viewModel.responseData.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    loadingDialog.setText(dataState.string?:"탈퇴 처리 중")
                    loadingDialog.show()
                }
                is DataState.Success -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireActivity(), dataState.result, Toast.LENGTH_SHORT).show()
                }
                is DataState.Failure -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireActivity(), dataState.string, Toast.LENGTH_SHORT).show()
                }
                is DataState.Error -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireActivity(), dataState.exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}