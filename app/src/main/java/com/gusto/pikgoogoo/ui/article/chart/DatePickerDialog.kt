package com.gusto.pikgoogoo.ui.article.chart

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.databinding.DialogDatePickerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DatePickerDialog(
    private var startDate: String,
    private var endDate: String,
    private val createdDate: String
) : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogDatePickerBinding
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val sCal = Calendar.getInstance()
    private val eCal = Calendar.getInstance()

    private val viewModel: ArticleChartViewModel by viewModels({ requireParentFragment() })

    init {
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))
        sCal.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        eCal.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        sCal.time = sdf.parse(startDate)
        eCal.time = sdf.parse(endDate)
    }

    override fun onStart() {
        dialog!!.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogDatePickerBinding.inflate(inflater, container, false)

        val v = binding.root

        binding.tvSYear.text = sCal.get(Calendar.YEAR).toString()
        binding.tvSMonth.text = (sCal.get(Calendar.MONTH)+1).toString()
        binding.tvSDay.text = sCal.get(Calendar.DAY_OF_MONTH).toString()

        binding.tvEYear.text = eCal.get(Calendar.YEAR).toString()
        binding.tvEMonth.text = (eCal.get(Calendar.MONTH)+1).toString()
        binding.tvEDay.text = eCal.get(Calendar.DAY_OF_MONTH).toString()

        binding.ibASYear.setOnClickListener(this)
        binding.ibASMonth.setOnClickListener(this)
        binding.ibASDay.setOnClickListener(this)
        binding.ibAEYear.setOnClickListener(this)
        binding.ibAEMonth.setOnClickListener(this)
        binding.ibAEDay.setOnClickListener(this)
        binding.ibMSYear.setOnClickListener(this)
        binding.ibMSMonth.setOnClickListener(this)
        binding.ibMSDay.setOnClickListener(this)
        binding.ibMEYear.setOnClickListener(this)
        binding.ibMEMonth.setOnClickListener(this)
        binding.ibMEDay.setOnClickListener(this)

        binding.bSetTerm.setOnClickListener {

            var allow: Boolean = true

            if (sCal.time < sdf.parse(createdDate)) {
                allow = false
                sCal.time = sdf.parse(createdDate)
                binding.tvSYear.text = sCal.get(Calendar.YEAR).toString()
                binding.tvSMonth.text = (sCal.get(Calendar.MONTH)+1).toString()
                binding.tvSDay.text = sCal.get(Calendar.DAY_OF_MONTH).toString()
                Toast.makeText(requireActivity(), "시작일이 항목의 생성일보다 이전입니다, 시작일은 항목 생성일로 설정됩니다", Toast.LENGTH_SHORT).show()

            }

            if (eCal.time > Date()) {
                allow = false
                eCal.time = Date()
                binding.tvEYear.text = eCal.get(Calendar.YEAR).toString()
                binding.tvEMonth.text = (eCal.get(Calendar.MONTH)+1).toString()
                binding.tvEDay.text = eCal.get(Calendar.DAY_OF_MONTH).toString()
                Toast.makeText(requireActivity(), "종료일은 현재 날짜를 초과할 수 없습니다", Toast.LENGTH_SHORT).show()
            }

            if (eCal.time < sCal.time) {
                allow = false
                Toast.makeText(requireActivity(), "종료일이 시작일의 이전입니다, 확인하여 주십시오", Toast.LENGTH_SHORT).show()
            }

            var between: Long = 60*60*24
            between = between*1000
            between = between*730

            if (eCal.time.time - sCal.time.time > between) {
                allow = false
                Toast.makeText(requireActivity(), "종료일과 시작일의 차이를 730일 이내로 설정하여 주십시오", Toast.LENGTH_SHORT).show()
            }

            if (allow) {
                viewModel.setTerm(sdf.format(sCal.time), sdf.format(eCal.time))
                dismiss()
            }

        }

        return v

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.ib_a_s_year -> {
                    sCal.add(Calendar.YEAR, +1)
                }
                R.id.ib_m_s_year -> {
                    sCal.add(Calendar.YEAR, -1)
                }
                R.id.ib_a_s_month -> {
                    sCal.add(Calendar.MONTH, +1)
                }
                R.id.ib_m_s_month -> {
                    sCal.add(Calendar.MONTH, -1)
                }
                R.id.ib_a_s_day -> {
                    sCal.add(Calendar.DAY_OF_MONTH, +1)
                }
                R.id.ib_m_s_day -> {
                    sCal.add(Calendar.DAY_OF_MONTH, -1)
                }
                R.id.ib_a_e_year -> {
                    eCal.add(Calendar.YEAR, +1)
                }
                R.id.ib_m_e_year -> {
                    eCal.add(Calendar.YEAR, -1)
                }
                R.id.ib_a_e_month -> {
                    eCal.add(Calendar.MONTH, +1)
                }
                R.id.ib_m_e_month -> {
                    eCal.add(Calendar.MONTH, -1)
                }
                R.id.ib_a_e_day -> {
                    eCal.add(Calendar.DAY_OF_MONTH, +1)
                }
                R.id.ib_m_e_day -> {
                    eCal.add(Calendar.DAY_OF_MONTH, -1)
                }
            }
            binding.tvSYear.text = sCal.get(Calendar.YEAR).toString()
            binding.tvSMonth.text = (sCal.get(Calendar.MONTH)+1).toString()
            binding.tvSDay.text = sCal.get(Calendar.DAY_OF_MONTH).toString()

            binding.tvEYear.text = eCal.get(Calendar.YEAR).toString()
            binding.tvEMonth.text = (eCal.get(Calendar.MONTH)+1).toString()
            binding.tvEDay.text = eCal.get(Calendar.DAY_OF_MONTH).toString()
        }
    }
}