package com.gusto.pikgoogoo.ui.subject.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.gusto.pikgoogoo.adapter.SubjectAdapter
import com.gusto.pikgoogoo.databinding.FragmentMySubjectsBinding
import com.gusto.pikgoogoo.ui.article.list.ArticleListFragment
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySubjectListFragment : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentMySubjectsBinding
    private val viewModel: MySubjectListViewModel by viewModels()

    private lateinit var adapter: SubjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMySubjectsBinding.inflate(inflater, container, false)
        val v = binding.root

        adapter = SubjectAdapter(
            { item, pos ->
                (requireActivity() as MainActivity)
                    .showFragmentOnFullScreen(ArticleListFragment(item.id, item.title, "MY_SUBJECTS"), "ARTICLE_LIST", false)
            },
            { itemCategoryId -> }
        )

        binding.rvMySubjects.adapter = adapter
        binding.rvMySubjects.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !recyclerView.canScrollVertically(1) && !isLoading && viewModel.moreFlag) {
                    viewModel.params.offset += 1
                    viewModel.getMySubjects(requireActivity())
                }
            }
        })

        viewModel.getGrade()
        binding.bBack.setOnClickListener { (parentFragmentManager.popBackStackImmediate()) }

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.subjectsData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    adapter.submitList(dataState.result)
                }
                is DataState.Failure -> {
                    loadEnd()
                    Toast.makeText(requireActivity(), dataState.string, Toast.LENGTH_LONG).show()
                }
                is DataState.Error -> {
                    loadEnd()
                    Toast.makeText(requireActivity(), dataState.exception.localizedMessage, Toast.LENGTH_LONG).show()
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
                    viewModel.params.offset = 0
                    viewModel.params.order = 0
                    viewModel.getMySubjects(requireActivity())
                }
                is DataState.Failure -> {
                    loadEnd()
                }
                is DataState.Error -> {
                    loadEnd()
                }
            }
        })
    }

}