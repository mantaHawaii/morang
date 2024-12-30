package com.gusto.pikgoogoo.ui.comment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.adapter.CommentAdapter
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.FragmentCommentListBinding
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import com.gusto.pikgoogoo.util.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentListFragment
constructor(
    val subjectId: Int,
    val articleId: Int = 0,
    val articleContent: String? = null
) : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentCommentListBinding
    private val viewModel: CommentListViewModel by viewModels()

    private lateinit var mAdapter: CommentAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAnalytics = Firebase.analytics

        binding = FragmentCommentListBinding.inflate(inflater, container, false)
        val v = binding.root

        val myId = viewModel.getMyUid(requireActivity())
        viewModel.params.subjectId = subjectId
        viewModel.params.articleId = articleId

        mAdapter = CommentAdapter(myId, { flag, commentId, pos, itemArticleId ->
            when(flag) {
                1 -> {
                    if (loginManager.isLoggedIn()) {
                        viewModel.likeComment(commentId)
                    } else {
                        alertLogin()
                    }
                }
                2 -> {
                    viewModel.params.articleId = itemArticleId
                    viewModel.params.order = 0
                    viewModel.params.offset = 0
                    viewModel.getComments()
                }
                else -> {

                }
            }
        }, { pos, commentId, comment, itemArticle, userId ->
            showChildFragment(CommentBehaviorDialog(pos, commentId, comment, itemArticle, myId == userId), FragmentTags.COMMENT_BEHAVIOR_TAG)
        })

        binding.rvComments.adapter = mAdapter

        binding.rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !recyclerView.canScrollVertically(1) && !isLoading && viewModel.moreFlag) {
                    viewModel.params.offset += 1
                    viewModel.getComments()
                }
            }
        })

        binding.ibSend.setOnClickListener {
            if (loginManager.isLoggedIn()) {
                val comment = binding.etComment.text.toString()
                if (comment.trim().length < 1) {
                    showMessage("댓글 내용이 비어있습니다")
                } else {
                    viewModel.commentOnArticle(articleId, comment)
                }
            } else {
                alertLogin()
            }
        }

        viewModel.getGrade()

        binding.cvCommentOrder.setOnClickListener {
            if (viewModel.params.order == 0) {
                firebaseAnalytics.logEvent("view_comments_best") {
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "comment")
                    param(FirebaseAnalytics.Param.METHOD, "best")
                }
                viewModel.params.offset = 0
                viewModel.params.order = 1
                viewModel.getComments()
            } else {
                viewModel.params.offset = 0
                viewModel.params.order = 0
                viewModel.getComments()
            }
        }

        binding.ibCloseFas.setOnClickListener {
            backPressed(FragmentExitStyle.SLIDE_AWAY)
        }

        binding.etComment.setOnFocusChangeListener { v, hasFocus ->
            if (articleContent != null && articleId > 0) {
                binding.tvArticle.text = "@"+articleContent
                if (hasFocus) {
                    binding.tvArticle.visibility = View.VISIBLE
                } else {
                    binding.tvArticle.visibility = View.GONE
                }
            }
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    fun showChildFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fcv_clf, fragment, tag)
            .addToBackStack(tag)
            .commit()
        binding.fcvClf.visibility = View.VISIBLE
    }

    private fun subscribeObservers() {
        viewModel.commentData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    mAdapter.setList(dataState.result)
                    if (viewModel.params.order == 1) {
                        binding.tvCommentOrder.text = "최신댓글"
                    } else {
                        binding.tvCommentOrder.text = "베스트댓글"
                    }
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
        viewModel.commentOnRes.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    (requireActivity() as MainActivity).hideKeyboard()
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    emptyEditText()
                    viewModel.getComments()
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
        viewModel.gradeData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    mAdapter.setGradeList(dataState.result)
                    viewModel.getComments()
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

    fun emptyEditText() {
        binding.etComment.text.clear()
    }

    fun alertLogin() {
        (requireActivity() as MainActivity).alertLogin()
    }

}