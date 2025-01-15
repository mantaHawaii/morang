package com.gusto.pikgoogoo.ui.article.chart

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.adapter.VoteHistoryAdapter
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.FragmentArticleChartBinding
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import com.gusto.pikgoogoo.util.linegraph.Line
import com.gusto.pikgoogoo.util.linegraph.LinePoint
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ArticleChartFragment
constructor(
    private val articleId: Int
) : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentArticleChartBinding
    private val viewModel: ArticleChartViewModel by viewModels()
    private lateinit var adapter: VoteHistoryAdapter
    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var createdDate: Date
    private lateinit var startDate: String
    private lateinit var endDate: String

    private var isSubscribedObserver = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentArticleChartBinding.inflate(inflater, container, false)
        val v = binding.root

        binding.lcVote.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener false
        }

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))

        val lcVote = binding.lcVote
        adapter = VoteHistoryAdapter()

        val l = Line()
        l.addPoint(LinePoint(0f, 5f))

        lcVote.addLine(l)

        val activity = requireActivity()
        l.color = ContextCompat.getColor(activity, R.color.spun_sugar)
        val defaultPadding = (lcVote.maxY - lcVote.minY)/10f
        lcVote.setRangeY(lcVote.minY-defaultPadding, lcVote.maxY+defaultPadding)

        lcVote.lineToFill = -1
        lcVote.sidePadding = dpToPx(activity, 20f)
        lcVote.pointColor = ContextCompat.getColor(activity, R.color.spun_sugar)
        lcVote.pointSize = dpToPx(activity, 5.5f)
        lcVote.clickRange = dpToPx(activity, 20f)
        lcVote.strokeWidth = dpToPx(activity, 3.5f)
        lcVote.setShowBottomLine(false)
        lcVote.labelSize = dpToPx(activity, 15f).toInt()
        lcVote.setBottomTextBold(true)
        lcVote.setMinYZero(false)
        lcVote.textBoxPadding = dpToPx(activity, 10f).toInt()
        lcVote.bottomTextColor = ContextCompat.getColor(activity, R.color.text_grey)
        lcVote.labelSidePadding = dpToPx(activity, 10f)
        lcVote.labelBottomPadding = dpToPx(activity, 5f)
        lcVote.guideStrokeColor = ContextCompat.getColor(activity, R.color.text_light_grey)
        lcVote.guideTextColor = ContextCompat.getColor(activity, R.color.text_grey)
        lcVote.pointTextColor = ContextCompat.getColor(activity, R.color.text_dark_grey)
        lcVote.textBoxColor = ContextCompat.getColor(activity, R.color.text_grey)
        lcVote.maxTextBoxColor = ContextCompat.getColor(activity, R.color.innuendo)
        lcVote.setOnPointClickedListener { lineIndex, pointIndex ->

        }
        lcVote.boxTextSize = dpToPx(activity, 15f)
        lcVote.guideTextSize = dpToPx(activity, 12f)
        adapter.lineColor = ContextCompat.getColor(activity, R.color.spun_sugar)
        lcVote.setAdapter(adapter)

        viewModel.getArticleCreatedDate(articleId)

        binding.bSetTerm.setOnClickListener {
            if (this::startDate.isInitialized && this::endDate.isInitialized) {
                val dialog = DatePickerDialog(startDate, endDate, sdf.format(createdDate))
                dialog.show(childFragmentManager, FragmentTags.DATE_PICKER_TAG)
            }
        }

        binding.ibCloseFas.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSubscribedObserver) {
            subscribeObservers()
        }
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    fun subscribeObservers() {
        isSubscribedObserver = true
        viewModel.createdDate.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    createdDate = sdf.parse(dataState.result)
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                    cal.time = Date()
                    cal.add(Calendar.DATE, -6)

                    var startDate = ""
                    var endDate = sdf.format(Date())

                    if (cal.time < createdDate) {
                        startDate = sdf.format(createdDate)
                    } else {
                        startDate = sdf.format(cal.time)
                    }
                    binding.tvTerm.text = startDate+" ~ "+endDate
                    viewModel.setTerm(startDate, endDate)
                }
                is DataState.Error -> {
                    loadEnd()
                    Toast.makeText(requireActivity(), dataState.exception.localizedMessage?:"에러", Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.term.observe(viewLifecycleOwner, Observer { term ->
            startDate = term.first
            endDate = term.second
            adapter.setStartEnd(term.first, term.second)
            viewModel.getVoteHistory(articleId, term.first, term.second)
            binding.tvTerm.text = startDate+" ~ "+endDate
        })
        viewModel.voteHistoryData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    adapter.setList(dataState.result)
                    binding.tvAllVoteCount.text = adapter.allVoteCount.toString()
                    binding.tvAvgVoteCount.text = (Math.round(adapter.avgVoteCount*100f)/100f).toString()
                }
                is DataState.Error -> {
                    loadEnd()
                    Toast.makeText(requireActivity(), dataState.exception.localizedMessage?:"에러", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}