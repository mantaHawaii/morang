package com.gusto.pikgoogoo.ui.profile.edit

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.databinding.FragmentEditProfileBinding
import com.gusto.pikgoogoo.ui.LoadingDialog
import com.gusto.pikgoogoo.ui.myinfo.MyInfoFragment
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment
constructor(
    val currentNickname: String
) : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val v = binding.root

        binding.bBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }

        binding.etNickname.setText(currentNickname)

        binding.bSubmit.setOnClickListener {
            editUser()
        }

        binding.etNickname.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                editUser()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        loadingDialog = LoadingDialog(requireActivity())

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    private fun editUser() {
        val nickname = binding.etNickname.text.toString().trim()
        if (nickname.length < 1 || nickname.length > 13) {
            showMessage("닉네임은 1자 이상, 13자 이하로 하여 주십시오")
        } else {
            viewModel.editProfileNickname(nickname)
        }
    }

    fun subscribeObservers() {
        viewModel.responseData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    showMessage(dataState.result)
                    (parentFragment as MyInfoFragment).refresh()
                    parentFragmentManager.popBackStackImmediate()
                }
                is DataState.Failure -> {
                    loadEnd()
                    showMessage(dataState.string)
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
    }

}