package com.gusto.pikgoogoo.ui.report

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.ReportReason
import com.gusto.pikgoogoo.databinding.DialogReportBinding
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportDialog
constructor(
    private val itemId: Int,
    private val type: Int
) : DialogFragment() {

    private lateinit var binding: DialogReportBinding
    private val viewModel: ReportViewModel by viewModels()
    val TYPE_ARTICLE = 1
    val TYPE_COMMENT = 2

    override fun onStart() {
        dialog!!.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogReportBinding.inflate(inflater, container, false)
        val v = binding.root

        viewModel.getReportReasons(type)

        binding.bSubmit.setOnClickListener {
            val idx = binding.rgReports.checkedRadioButtonId-1
            if (idx >= 0) {
                val checkedItem = binding.rgReports.get(idx)
                when (type) {
                    TYPE_ARTICLE -> {
                        viewModel.reportArticle(requireActivity(), itemId, checkedItem.tag as Int)
                    }
                    TYPE_COMMENT -> {
                        viewModel.reportComment(requireActivity(), itemId, checkedItem.tag as Int)
                    }
                    else -> { }
                }
            } else {
                Toast.makeText(requireActivity(), "신고 사유를 선택하여 주십시오", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ibCloseFas.setOnClickListener { dismiss() }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.reportData.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> { }
                is DataState.Success -> {
                    for (item in dataState.result) {
                        attachRadiobutton(item, binding.rgReports)
                    }
                }
                is DataState.Failure -> {
                    Toast.makeText(requireActivity(), dataState.string, Toast.LENGTH_SHORT).show()
                }
                is DataState.Error -> {
                    Toast.makeText(requireActivity(), dataState.exception.localizedMessage?:"에러", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.messageData.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> { }
                is DataState.Success -> {
                    Toast.makeText(requireActivity(), dataState.result, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is DataState.Failure -> {
                    Toast.makeText(requireActivity(), dataState.string, Toast.LENGTH_SHORT).show()
                }
                is DataState.Error -> {
                    Toast.makeText(requireActivity(), dataState.exception.localizedMessage?:"에러", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun attachRadiobutton(item: ReportReason, group: RadioGroup) {
        val rb = RadioButton(requireActivity())
        rb.text = item.reason
        rb.tag = item.id
        rb.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        rb.setPadding(
            0,
            dpToPx(requireActivity(), 12f).toInt(),
            0,
            dpToPx(requireActivity(), 12f).toInt()
        )
        rb.setTextColor(ContextCompat.getColor(requireActivity(), R.color.text_dark_grey))
        group.addView(rb)
        group.invalidate()
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

}