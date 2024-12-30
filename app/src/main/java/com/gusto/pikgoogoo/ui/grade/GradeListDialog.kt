package com.gusto.pikgoogoo.ui.grade

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.adapter.GradeAdapter
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.databinding.DialogGradeListBinding
import com.gusto.pikgoogoo.ui.myinfo.MyInfoViewModel
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class GradeListDialog
constructor(
    private val myId: Int,
    private val myGrade: Int,
    private val myGradeIcon: Int
) : DialogFragment(R.layout.dialog_grade_list) {

    private lateinit var binding: DialogGradeListBinding
    private val parentViewModel: MyInfoViewModel by viewModels({ requireParentFragment() })
    private val viewModel: GradeViewModel by viewModels()

    private lateinit var adapter: GradeAdapter

    override fun onStart() {
        dialog!!.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogGradeListBinding.inflate(inflater, container, false)
        val v = binding.root

        adapter = GradeAdapter({ id ->
            viewModel.setGradeIcon(id)
        }, myGrade, myGradeIcon)

        binding.rvGrade.adapter = adapter

        binding.rvGrade.layoutManager = GridLayoutManager(requireActivity(), 5)

        viewModel.getGrade()

        binding.ibClose.setOnClickListener { dismiss() }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.gradeData.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                }
                is DataState.Success -> {
                    adapter.setList(dataState.result)
                }
                is DataState.Failure -> {
                    showMessage(dataState.string)
                }
                is DataState.Error -> {
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
        viewModel.gradeIconData.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    parentViewModel.getMyUserData(requireActivity())
                    parentFragmentManager.popBackStackImmediate()
                    dismiss()
                }
                is DataState.Failure -> {
                    showMessage(dataState.string)
                }
                is DataState.Error -> {
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

}