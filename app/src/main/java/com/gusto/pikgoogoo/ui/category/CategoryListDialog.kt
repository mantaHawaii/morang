package com.gusto.pikgoogoo.ui.category

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
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.adapter.CategoryAdapter
import com.gusto.pikgoogoo.data.Category
import com.gusto.pikgoogoo.databinding.DialogCategoriesBinding
import com.gusto.pikgoogoo.ui.subject.list.SubjectListViewModel
import com.gusto.pikgoogoo.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryListDialog : DialogFragment(R.layout.dialog_categories) {

    private lateinit var binding: DialogCategoriesBinding
    private val viewModel: CategoryListViewModel by viewModels()
    private val parentViewModel: SubjectListViewModel by viewModels({requireParentFragment()})
    private var isSubscribedObservers = false

    private lateinit var adapter: CategoryAdapter

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
        binding = DialogCategoriesBinding.inflate(inflater, container, false)
        val v = binding.root

        adapter = CategoryAdapter(mutableListOf(), { categoryId ->
            parentViewModel.params.categoryId = categoryId
            parentViewModel.params.offset = 0
            parentViewModel.fetchSubjects()
            dismiss()
        })

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSubscribedObservers) {
            subscribeObservers()
        }
        viewModel.getCategories()
    }

    fun subscribeObservers() {
        isSubscribedObservers = true
        viewModel.categoriesData.observe(this, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                }
                is DataState.Success -> {
                    adapter.list.clear()
                    adapter.list.add(0, Category(0, "전체"))
                    adapter.list.addAll(dataState.result)
                    binding.rvCategoires.adapter = adapter
                }
                is DataState.Error -> {
                    Toast.makeText(requireActivity(), dataState.exception.localizedMessage?:"에러", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}