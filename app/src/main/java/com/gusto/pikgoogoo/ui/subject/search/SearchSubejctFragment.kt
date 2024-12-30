package com.gusto.pikgoogoo.ui.subject.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.gusto.pikgoogoo.adapter.SearchWordAdapter
import com.gusto.pikgoogoo.databinding.FragmentSearchSubjectBinding
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.ui.subject.list.SubjectListViewModel
import com.gusto.pikgoogoo.ui.components.fragment.BackPressableFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchSubejctFragment : BackPressableFragment() {

    private lateinit var binding: FragmentSearchSubjectBinding
    private lateinit var adapter: SearchWordAdapter

    private val parentViewModel: SubjectListViewModel by viewModels({ requireParentFragment() })
    private val viewModel: SearchSubjectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchSubjectBinding.inflate(inflater, container, false)
        val v = binding.root

        val list = viewModel.getSearchHistory(requireActivity())

        adapter = SearchWordAdapter(list, { t, pos, words ->
            when(t) {
                0 -> {
                    search(words)
                }
                1 -> {
                    viewModel.removeFromSearchHistory(requireActivity(), pos)
                }
            }
        })
        binding.rvRecent.adapter = adapter

        binding.etSearchwords.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                (requireActivity() as MainActivity).hideKeyboard()

                val searchWords = binding.etSearchwords.text.toString().trim()
                search(searchWords)

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.etSearchwords.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length > 0) {
                    binding.ibErase.visibility = View.VISIBLE
                } else {
                    binding.ibErase.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.ibErase.setOnClickListener {
            binding.etSearchwords.setText("")
        }

        binding.bBack.setOnClickListener { parentFragmentManager.popBackStackImmediate() }

        return v
    }

    private fun search(words: String) {
        viewModel.saveSearchHistory(requireActivity(), words)
        parentViewModel.params.categoryId = 0
        parentViewModel.params.offset = 0
        parentViewModel.params.searchWords = words
        parentViewModel.fetchSubjects()
    }

}