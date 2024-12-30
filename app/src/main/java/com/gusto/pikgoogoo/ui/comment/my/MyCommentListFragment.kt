package com.gusto.pikgoogoo.ui.comment.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.adapter.CommentAdapter
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.FragmentMyCommentListBinding
import com.gusto.pikgoogoo.ui.comment.CommentBehaviorDialog
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCommentListFragment : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentMyCommentListBinding
    private val viewModel: MyCommentListViewModel by viewModels()

    private lateinit var adapter: CommentAdapter

    private var myUid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myUid = viewModel.getUid(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyCommentListBinding.inflate(inflater, container, false)
        val v = binding.root

        binding.fcvMclf.visibility = View.VISIBLE

        adapter = CommentAdapter(myUid, { flag, commentId, pos, itemArticleId ->

        }, { pos, commentId, comment, itemArticle, userId ->
            showChildFragment(CommentBehaviorDialog(pos, commentId, comment, itemArticle, myUid == userId), FragmentTags.COMMENT_BEHAVIOR_TAG)
        })

        binding.rvComments.adapter = adapter

        binding.rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1) && !isLoading && newState== RecyclerView.SCROLL_STATE_IDLE && viewModel.moreFlag) {
                    viewModel.params.offset += 1
                    viewModel.getMyComments(requireActivity())
                }
            }
        })

        viewModel.getGrade()

        binding.bBack.setOnClickListener { backPressed() }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    fun showChildFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fcv_mclf, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun subscribeObservers() {
        viewModel.commentData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    adapter.setList(dataState.result)
                }
                is DataState.Failure -> {
                    loadEnd()
                    Toast.makeText(requireContext(), dataState.string, Toast.LENGTH_LONG).show()
                }
                is DataState.Error -> {
                    loadEnd()
                    Toast.makeText(requireContext(), dataState.exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.responseData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    Toast.makeText(requireActivity(), dataState.result, Toast.LENGTH_SHORT).show()
                }
                is DataState.Failure -> {
                    loadEnd()
                    Toast.makeText(requireContext(), dataState.string, Toast.LENGTH_LONG).show()
                }
                is DataState.Error -> {
                    loadEnd()
                    Toast.makeText(requireContext(), dataState.exception.localizedMessage, Toast.LENGTH_LONG).show()
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
                    adapter.setGradeList(dataState.result)
                    viewModel.getMyComments(requireActivity())
                }
                is DataState.Failure -> {
                    loadEnd()
                    Toast.makeText(requireContext(), dataState.string, Toast.LENGTH_LONG).show()
                }
                is DataState.Error -> {
                    loadEnd()
                    Toast.makeText(requireContext(), dataState.exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}