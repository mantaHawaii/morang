package com.gusto.pikgoogoo.ui.subject.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.adapter.OrderSpinnerAdapter
import com.gusto.pikgoogoo.adapter.SubjectAdapter
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.FragmentSubjectListBinding
import com.gusto.pikgoogoo.ui.article.list.ArticleListFragment
import com.gusto.pikgoogoo.ui.category.CategoryListDialog
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.ui.myinfo.MyInfoFragment
import com.gusto.pikgoogoo.ui.subject.add.AddSubjectFragment
import com.gusto.pikgoogoo.ui.subject.search.SearchSubejctFragment
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import com.gusto.pikgoogoo.util.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubjectListFragment : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentSubjectListBinding
    private val viewModel: SubjectListViewModel by viewModels()

    private lateinit var adapter: SubjectAdapter

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var loginManager: LoginManager

    private var isSubscribedObservers = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSubjectListBinding.inflate(inflater, container, false)
        val v = binding.root

        binding.fcvFsl.visibility = View.VISIBLE

        firebaseAnalytics = Firebase.analytics

        binding.fabAddSubejct.setOnClickListener {
            if (loginManager.isLoggedIn()) {
                showChildFragment(
                    AddSubjectFragment(),
                    FragmentTags.ADD_SUBJECT_TAG
                )
            } else {
                alertLogin()
            }
        }

        adapter = SubjectAdapter(
            { item, pos ->
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_ID, item.id.toLong())
                    param(FirebaseAnalytics.Param.ITEM_NAME, item.title)
                }
                showChildFragment(
                    ArticleListFragment(
                        item.id,
                        item.title
                    ), FragmentTags.ARTICLE_LIST_TAG
                )
            },
            { itemCategoryId ->
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST) {
                    param(FirebaseAnalytics.Param.ITEM_LIST_ID, itemCategoryId.toLong())
                }
                viewModel.params.categoryId = itemCategoryId
                viewModel.params.offset = 0
                viewModel.fetchSubjects()
            }
        )

        binding.rvSubjects.adapter = adapter
        binding.rvSubjects.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.fabAddSubejct.shrink()
                } else if (dy < -20) {
                    binding.fabAddSubejct.extend()
                }
                if (dy > 0 && !recyclerView.canScrollVertically(1) && !isLoading && viewModel.moreFlag) {
                    viewModel.params.offset += 1
                    viewModel.fetchSubjects()
                }
            }
        })

        binding.srlSubjects.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST) {
                    param(FirebaseAnalytics.Param.ITEM_LIST_NAME, "SUBJECTS_REFRESH")
                }
                viewModel.params.categoryId = 0
                viewModel.params.offset = 0
                viewModel.params.searchWords = ""
                viewModel.fetchSubjects()
            }
        })

        binding.ibAccocunt.setOnClickListener {
            if (loginManager.isLoggedIn()) {
                showChildFragment(
                    MyInfoFragment(),
                    FragmentTags.MY_INFO_TAG
                )
            } else {
                alertLogin()
            }
        }


        binding.ibSearch.setOnClickListener {
            showChildFragment(SearchSubejctFragment(), FragmentTags.SEARCH_SUBJECT_TAG)
        }

        viewModel.getGrade()

        binding.llCategories.setOnClickListener {
            CategoryListDialog().show(childFragmentManager, FragmentTags.CATEGORY_LIST_TAG)
        }

        binding.spOrder.adapter = OrderSpinnerAdapter(requireActivity(), arrayOf("인기순", "최신순", "오래된순"))
        binding.ibOrder.setOnClickListener { binding.spOrder.performClick() }
        binding.spOrder.setSelection(viewModel.params.order, false)
        binding.spOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.params.order = position
                viewModel.params.offset = 0
                viewModel.fetchSubjects()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSubscribedObservers) { subscribeObservers() }
    }

    fun subscribeObservers() {
        isSubscribedObservers = true
        viewModel.subjectsData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    adapter.submitList(dataState.result)
                    adapter.notifyDataSetChanged()
                    binding.srlSubjects.isRefreshing = false
                }
                is DataState.Error -> {
                    loadEnd()
                    binding.srlSubjects.isRefreshing = false
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
                    viewModel.fetchSubjects()
                }
                is DataState.Error -> {
                    loadEnd()
                }
            }
        })
    }

    override fun backPressed(exitStyle: FragmentExitStyle) {
        super.backPressed(exitStyle)
        requireActivity().finish()
    }

    fun showChildFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fcv_fsl, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun alertLogin() {
        (requireActivity() as MainActivity).alertLogin()
    }

}