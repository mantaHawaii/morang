package com.gusto.pikgoogoo.ui.article.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.adapter.ArticleAdapter
import com.gusto.pikgoogoo.data.ArticleOrder
import com.gusto.pikgoogoo.data.tag.FragmentTags
import com.gusto.pikgoogoo.databinding.FragmentArticleListBinding
import com.gusto.pikgoogoo.ui.article.add.AddArticleFragment
import com.gusto.pikgoogoo.ui.comment.CommentListFragment
import com.gusto.pikgoogoo.ui.main.MainActivity
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import com.gusto.pikgoogoo.util.LoginManager
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class ArticleListFragment
constructor(
    private val subjectId: Int,
    private val title: String,
    private val from: String = "MAIN"
) : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentArticleListBinding
    private val viewModel: ArticleListViewModel by viewModels()

    private lateinit var articleAdapter: ArticleAdapter

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val mNativeAds = mutableListOf<NativeAd>()

    private var isFirst: Boolean = true

    private var isSubscribedObservers = false

    private var lastInsertedAdIndex = 0

    private var moreFlag = true

    private var lastSize = 0

    private var scrollTopFlag = false

    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        MobileAds.initialize(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.params.subjectId = subjectId

        firebaseAnalytics.logEvent("view_subject") {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "subject")
        }

        binding = FragmentArticleListBinding.inflate(inflater, container, false)
        val v = binding.root

        binding.fabAddArticle.setOnClickListener {
            if (loginManager.isLoggedIn()) {
                openChildFragment(AddArticleFragment(subjectId = subjectId), FragmentTags.ADD_ARTICLE_TAG)
            } else {
                alertLogin()
            }
        }
        if (mNativeAds.isEmpty()) {
            viewModel.fetchAds(requireActivity(), 3)
        }
        articleAdapter = ArticleAdapter(requireActivity(), mutableListOf()) { type, pos, articleId, content ->
            when(type) {
                ArticleAdapter.ActionType.VOTE.value -> {
                    if (loginManager.isLoggedIn()) {
                        voteArticle(articleId)
                    } else {
                        alertLogin()
                    }
                }
                ArticleAdapter.ActionType.COMMENT.value -> {
                    firebaseAnalytics.logEvent("view_comments_from_article") {
                        param(FirebaseAnalytics.Param.CONTENT_TYPE, "comment")
                        param(FirebaseAnalytics.Param.METHOD, "article")
                    }
                    showComments(articleId, content)
                }
                ArticleAdapter.ActionType.ROOT.value -> {
                    ArticleBehaviorDialog(pos, articleId, content).show(childFragmentManager, FragmentTags.ARTICLE_BEHAVIOR_TAG)
                }
            }
        }

        binding.rvArticles.adapter = articleAdapter
        binding.rvArticles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    binding.fabAddArticle.shrink()
                } else if (dy < -20) {
                    binding.fabAddArticle.extend()
                }

                if (dy > 0 && !recyclerView.canScrollVertically(1) && !isLoading && moreFlag) {
                    Log.d("MR_ALF", "offset 변화 후 불러옵니다:"+ viewModel.params.offset.toString())
                    scrollTopFlag = false
                    viewModel.params.offset += 1
                    viewModel.fetchArticles()
                }
            }
        })

        binding.llAllComment.setOnClickListener {
            firebaseAnalytics.logEvent("view_comments_from_all") {
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "comment")
                param(FirebaseAnalytics.Param.METHOD, "all")
            }
            openChildFragment(CommentListFragment(subjectId, 0), FragmentTags.COMMENT_LIST_TAG)
        }

        binding.tlTerm.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                clearSearch()
                tab?.let {
                    scrollTopFlag = true
                    viewModel.params.order = tapPositionToRequestOrder(it.position)
                    viewModel.params.offset = 0
                    viewModel.params.searchWords = ""
                    viewModel.fetchArticles()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    scrollTopFlag = true
                    viewModel.params.order = tapPositionToRequestOrder(it.position)
                    viewModel.params.offset = 0
                    viewModel.params.searchWords = ""
                    viewModel.fetchArticles()
                }
            }

        })

        if (isFirst) {
            val tab = binding.tlTerm.getTabAt(0)
            if (tab != null) {
                tab.select()
            } else {
                scrollTopFlag = true
                viewModel.fetchArticles()
            }
            isFirst = false
        }

        binding.tvTitle.setText(title)

        binding.etSearchwords.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                scrollTopFlag = true
                viewModel.params.offset = 0
                viewModel.params.searchWords = binding.etSearchwords.text.toString().trim()
                viewModel.fetchArticles()
                return@setOnEditorActionListener false
            }
            return@setOnEditorActionListener false
        }

        binding.bBack.setOnClickListener { backPressed() }

        binding.srlArticles.setOnRefreshListener { refreshArticles() }

        viewModel.fetchBookmarkStatus(requireActivity())

        binding.ibBookmark.setOnClickListener {
            if (loginManager.isLoggedIn()) {
                viewModel.addToBookmarks()
            } else {
                alertLogin()
            }
        }

        binding.bShare.setOnClickListener {
            Firebase.dynamicLinks.shortLinkAsync {
                val url = URLEncoder.encode("https://morang.page.link?subjectId="+subjectId+"&title="+title, "UTF-8")
                longLink = Uri.parse("https://morang.page.link/?link="+url
                        +"&apn=com.gusto.pikgoogoo"
                        +"&st="+URLEncoder.encode(title, "UTF-8")
                        +"&sd="+URLEncoder.encode("모랭 - 모든 랭킹", "UTF-8")
                )
            }.addOnSuccessListener { (shortLink, flowchartLink) ->

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    putExtra(Intent.EXTRA_TITLE, title+" - 지금 바로 랭킹을 확인하세요")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)

            }.addOnFailureListener { e ->
                showMessage(e.localizedMessage?:"에러")
            }

        }

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSubscribedObservers) { subscribeObservers() }
    }

    fun openChildFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fcv_fal, fragment, tag)
            .addToBackStack(tag)
            .commit()
        binding.fcvFal.visibility = View.VISIBLE
    }

    fun voteArticle(articleId: Int) {
        if (!isLoading) {
            viewModel.voteArticleSubmit(articleId)
        } else {
            showMessage("이전 요청을 처리 중입니다. 잠시 후에 시도하세요")
        }
    }

    fun showComments(articleId: Int, content: String) {
        openChildFragment(CommentListFragment(subjectId, articleId, content), FragmentTags.COMMENT_LIST_TAG)
    }

    fun clearSearch() {
        binding.etSearchwords.text.clear()
    }

    fun subscribeObservers() {
        isSubscribedObservers = true
        viewModel.voteRes.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    articleAdapter.notifyItemChanged(dataState.result.second, 1)
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
        viewModel.bookmarkRes.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    if (dataState.result.startsWith("BOOKMARK")) {
                        binding.ibBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                    } else if (dataState.result.startsWith("UNBOOKMARK")) {
                        binding.ibBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                    }
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
        viewModel.bookmarkData.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    if (dataState.result) {
                        binding.ibBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                    } else {
                        binding.ibBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                    }
                }
            }
        })
        viewModel.articlesData.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
                is DataState.Loading -> {
                    Log.d("MR_ALF", "항목 불러오는 중입니다")
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    val articles = dataState.result
                    moreFlag = articles.size != lastSize
                    articleAdapter.setList(articles)
                    lastSize = articleAdapter.itemCount
                    lastInsertedAdIndex = 0
                    insertAds()
                    binding.srlArticles.isRefreshing = false
                    if (scrollTopFlag) {
                        binding.rvArticles.smoothScrollToPosition(0)
                    }
                }
            }
        })
        viewModel.adsData.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    showMessage(dataState.exception.localizedMessage?:"error")
                }
                is DataState.Loading -> {}
                is DataState.Success -> {
                    val ad = dataState.result
                    mNativeAds.add(ad)
                    insertAd(ad)
                }
            }
        })
    }

    fun refreshArticles() {
        clearSearch()
        viewModel.params.searchWords = ""
        viewModel.params.offset = 0
        viewModel.fetchArticles()
    }

    fun alertLogin() {
        (requireActivity() as MainActivity).alertLogin()
    }

    private fun reset() {

    }

    private fun tapPositionToRequestOrder(tabPos: Int): Int {
        return when(tabPos) {
            0 -> ArticleOrder.ALL.value
            1 -> ArticleOrder.DAY.value
            2 -> ArticleOrder.WEEK.value
            3 -> ArticleOrder.MONTH.value
            4 -> ArticleOrder.NEW.value
            else -> ArticleOrder.ALL.value
        }
    }

    private fun insertAd(ad: NativeAd) {
        val minIndexDistance = 7
        var index = 0
        if (lastInsertedAdIndex == 0) {
            val value1 = articleAdapter.itemCount-1
            val value2 = lastInsertedAdIndex+minIndexDistance
            index = minOf(value1, value2)
            if (index <= 0) {
                return
            }
        } else {
            index = lastInsertedAdIndex+minIndexDistance
        }
        try {
            articleAdapter.itemList.add(index, ad)
            articleAdapter.notifyItemInserted(index)
            lastInsertedAdIndex = index
        } catch (e: Exception) {
            Log.e("MR_ALF", e.localizedMessage?:"에러")
        }
    }

    private fun insertAds() {
        for (ad in mNativeAds) {
            insertAd(ad)
        }
    }

    override fun onDestroyView() {
        for (ad in mNativeAds) {
            ad.destroy()
        }
        super.onDestroyView()
    }

    override fun onDestroy() {
        for (ad in mNativeAds) {
            ad.destroy()
        }
        super.onDestroy()
    }

}