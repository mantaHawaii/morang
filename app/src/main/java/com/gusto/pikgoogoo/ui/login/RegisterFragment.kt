package com.gusto.pikgoogoo.ui.login

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.data.LoginCode
import com.gusto.pikgoogoo.databinding.FragmentRegistBinding
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : LoadingIndicatorFragment()  {

    private lateinit var binding: FragmentRegistBinding

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegistBinding.inflate(inflater, container, false)

        val v = binding.root

        binding.bRegist.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            viewModel.registerUser(nickname)
        }

        binding.etNickname.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val nickname = binding.etNickname.text.toString()
                viewModel.registerUser(nickname)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }
        
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.registerState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
                is DataState.Success -> {
                    loadEnd()
                    parentFragmentManager.popBackStackImmediate()
                }
            }
        })
    }

}