package com.gusto.pikgoogoo.ui.article.list

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.DialogArticleBehaviorBinding
import com.gusto.pikgoogoo.ui.article.chart.ArticleChartFragment
import com.gusto.pikgoogoo.ui.report.ReportDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleBehaviorDialog(
    val pos: Int,
    val articleId: Int,
    val content: String
) : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogArticleBehaviorBinding
    private val viewModel: ArticleListViewModel by viewModels({ requireParentFragment() })

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAnalytics = Firebase.analytics

        binding = DialogArticleBehaviorBinding.inflate(inflater, container, false)
        val v = binding.root

        binding.tvContent.text = content

        binding.llComment.setOnClickListener(this)
        binding.llVote.setOnClickListener(this)
        binding.llCopy.setOnClickListener(this)
        binding.llGraph.setOnClickListener(this)
        binding.llReport.setOnClickListener(this)

        return v
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.ll_comment -> {
                    firebaseAnalytics.logEvent("view_comments_from_behavior_dialog") {
                        param(FirebaseAnalytics.Param.CONTENT_TYPE, "comment")
                        param(FirebaseAnalytics.Param.METHOD, "behavior_dialog")
                    }
                    (parentFragment as ArticleListFragment).showComments(articleId, content)
                    dismiss()
                }
                R.id.ll_vote -> {
                    (parentFragment as ArticleListFragment).voteArticle(articleId)
                    dismiss()
                }
                R.id.ll_graph -> {
                    (parentFragment as ArticleListFragment).openChildFragment(ArticleChartFragment(articleId), FragmentTags.ARTICLE_CHART_TAG)
                    dismiss()
                }
                R.id.ll_copy -> {
                    val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("ARTICLE_CONTENT", content)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(requireActivity(), "\'"+content+"\'(이)가 클립보드에 복사되었습니다", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                R.id.ll_report -> {
                    ReportDialog(articleId, 1).show(childFragmentManager, FragmentTags.REPORT_TAG)
                }
                else -> {

                }
            }
        }
    }
}