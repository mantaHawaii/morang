package com.gusto.pikgoogoo.ui.comment.edit

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.gusto.pikgoogoo.databinding.DialogEditCommentBinding
import com.gusto.pikgoogoo.ui.comment.CommentListViewModel
import com.gusto.pikgoogoo.ui.components.fragment.BackPressableFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCommentDialog
constructor(
    private val pos: Int,
    private val commentId: Int,
    private val comment: String,
    private val itemArticle: String
) : DialogFragment() {

    private lateinit var binding: DialogEditCommentBinding
    private val parentViewModel: CommentListViewModel by viewModels({requireParentFragment().requireParentFragment()})

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
        binding = DialogEditCommentBinding.inflate(inflater, container, false)
        val v = binding.root

        if (!itemArticle.equals("")) {
            binding.tvArticle.visibility = View.VISIBLE
            binding.tvArticle.text = "@"+itemArticle
        } else {
            binding.tvArticle.visibility = View.GONE
        }

        binding.etComment.setText(comment)

        binding.bSubmit.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (!text.equals(comment) && !text.equals("")) {
                parentViewModel.updateComment(commentId, text)
                (requireParentFragment() as BackPressableFragment).backPressed()
            } else {
                if (text.equals(comment)) {
                    dismiss()
                } else {
                    Toast.makeText(requireActivity(), "댓글이 비어있습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.bExit.setOnClickListener { dismiss() }
        return v

    }

}