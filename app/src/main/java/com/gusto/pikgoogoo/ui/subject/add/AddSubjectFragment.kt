package com.gusto.pikgoogoo.ui.subject.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.adapter.CategorySpinnerAdapter
import com.gusto.pikgoogoo.data.Category
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.FragmentAddSubjectBinding
import com.gusto.pikgoogoo.ui.article.list.ArticleListFragment
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.ui.components.fragment.BackPressableFragment
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSubjectFragment : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentAddSubjectBinding
    private val viewModel: AddSubjectViewModel by viewModels()
    private lateinit var adapter: CategorySpinnerAdapter

    private var isSubscribedObservers = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddSubjectBinding.inflate(inflater, container, false)
        val v = binding.root

        adapter = CategorySpinnerAdapter(requireActivity())
        binding.spCategory.adapter = adapter

        binding.bSubmit.setOnClickListener {
            (requireActivity() as MainActivity).hideKeyboard()
            val titleString = binding.etTitle.text.toString()
            val categoryId = (binding.spCategory.selectedItem as Category).id
            if (titleString.length < 2) {
                showMessage("타이틀은 2글자 미만이어서는 안됩니다")
            } else {
                viewModel.insertSubject(titleString, categoryId)
            }
        }

        binding.ibCloseFas.setOnClickListener {
            backPressed(FragmentExitStyle.SLIDE_DOWN)
        }

        binding.spCategory.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    adapter.itemList.add(0, adapter.itemList.get(position))
                    adapter.itemList.removeAt(position + 1)
                    adapter.notifyDataSetChanged()
                    binding.spCategory.setSelection(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.etTitle.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener false
        }

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSubscribedObservers) {
            subscribeObservers()
        }
    }

    fun subscribeObservers() {
        isSubscribedObservers = true
        viewModel.categoriesData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    adapter.setList(dataState.result)
                    binding.spCategory.setSelection(dataState.result.size-1)
                }
                /*is DataState.Failure -> {
                    loadEnd()
                    showMessage(dataState.string)
                }*/
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
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
                    (requireActivity() as MainActivity).showFragmentOnFullScreen(
                        ArticleListFragment(dataState.result.second, dataState.result.first),
                        FragmentTags.ARTICLE_LIST_TAG,
                        false
                    )
                    parentFragmentManager.popBackStack(FragmentTags.ADD_SUBJECT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                /*is DataState.Failure -> {
                    loadEnd()
                    showMessage(dataState.string)
                }*/
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
    }

}