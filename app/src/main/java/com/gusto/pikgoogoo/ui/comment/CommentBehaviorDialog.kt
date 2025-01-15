package com.gusto.pikgoogoo.ui.comment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.DialogCommentBehaviorBinding
import com.gusto.pikgoogoo.ui.comment.edit.EditCommentDialog
import com.gusto.pikgoogoo.ui.report.ReportDialog
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import com.gusto.pikgoogoo.util.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentBehaviorDialog
constructor(
    private val pos: Int,
    private val commentId: Int,
    private val comment: String,
    private val itemArticle: String,
    private val isMine: Boolean = false
) : LoadingIndicatorFragment() {

    private lateinit var binding: DialogCommentBehaviorBinding
    private val parentViewModel: CommentListViewModel by viewModels({requireParentFragment()})

    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogCommentBehaviorBinding.inflate(inflater, container, false)
        val v = binding.root

        if (isMine) {

            binding.llDelete.visibility = View.VISIBLE
            binding.llEdit.visibility = View.VISIBLE
            binding.llReport.visibility = View.GONE

            binding.llEdit.setOnClickListener {
                EditCommentDialog(pos, commentId, comment, itemArticle).show(childFragmentManager, FragmentTags.EDIT_COMMENT_TAG)
            }

            binding.llDelete.setOnClickListener {
                parentViewModel.removeComment(commentId)
                backPressed()
            }

        } else {

            binding.llDelete.visibility = View.GONE
            binding.llEdit.visibility = View.GONE
            binding.llReport.visibility = View.VISIBLE

            binding.llReport.setOnClickListener {
                ReportDialog(commentId, 2).show(parentFragmentManager, FragmentTags.REPORT_TAG)
            }

        }

        binding.clBackground.setOnClickListener { backPressed(FragmentExitStyle.FADE_OUT) }

        val view = binding.cvBehavior
        var heightF = 0f

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                heightF = view.height.toFloat()
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view.visibility = View.GONE

                ValueAnimator.ofFloat(heightF, 0f).apply {
                    duration = 151
                    addUpdateListener { updatedAnimation ->
                        if (view.translationY >= heightF) {
                            view.visibility = View.VISIBLE
                        }
                        view.translationY = updatedAnimation.animatedValue as Float
                    }
                    start()
                }

            }
        })

        return v
    }

}