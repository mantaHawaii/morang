package com.gusto.pikgoogoo.ui.article.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
    private var totalVoteCount = 0

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val mNativeAds = mutableListOf<NativeAd>()

    private var isFirst: Boolean = true

    private var adLoader: AdLoader? = null

    private var adCount = 0

    private var isSubscribedObservers = false

    private lateinit var indices: List<Int>

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
    ): View? {

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
            loadNativeAds()
        }

        articleAdapter = ArticleAdapter(requireActivity(), mutableListOf()) { type, pos, articleId, content ->
            when(type) {
                0 -> {
                    if (loginManager.isLoggedIn()) {
                        voteArticle(pos, articleId)
                    } else {
                        alertLogin()
                    }
                }
                1 -> {
                    firebaseAnalytics.logEvent("view_comments_from_article") {
                        param(FirebaseAnalytics.Param.CONTENT_TYPE, "comment")
                        param(FirebaseAnalytics.Param.METHOD, "article")
                    }
                    showComments(articleId, content)
                }
                2 -> {
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

                if (dy > 0 && !recyclerView.canScrollVertically(1) && !isLoading && viewModel.moreFlag) {
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
                viewModel.fetchArticles()
            }
            isFirst = false
        }

        binding.tvTitle.setText(title)

        binding.etSearchwords.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.params.offset = 0
                viewModel.params.searchWords = binding.etSearchwords.text.toString().trim()
                viewModel.fetchArticles()
                return@setOnEditorActionListener false
            }
            return@setOnEditorActionListener false
        }

        binding.bBack.setOnClickListener { backPressed() }

        binding.srlArticles.setOnRefreshListener { refreshArticles() }

        viewModel.isBookmarked(requireActivity())

        binding.ibBookmark.setOnClickListener {
            if (loginManager.isLoggedIn()) {
                viewModel.bookmarkSubject()
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

    fun voteArticle(pos: Int, articleId: Int) {
        if (!isLoading) {
            viewModel.voteArticle(articleId, pos)
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

    private fun loadNativeAds() {
        mNativeAds.clear()
        adCount = 0
        val builder = AdLoader.Builder(requireActivity(), requireActivity().getString(R.string.ad_unit_id))
        adLoader = builder.forNativeAd { nativeAd ->
                mNativeAds.add(nativeAd)
                if (::indices.isInitialized && adCount < indices.size) {
                    insertAd(indices[adCount], nativeAd)
                }
                adCount++
            }
            .withAdListener(object: AdListener() {
                override fun onAdFailedToLoad(errorCode: LoadAdError) {
                    showMessage(errorCode.message)
                }
            })
            .build()
        adLoader!!.loadAds(AdRequest.Builder().build(), 3)
    }

    fun subscribeObservers() {
        isSubscribedObservers = true
        viewModel.articlesData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    articleAdapter.setList(dataState.result)
                    indices = getIndexesOfList(
                        listSize = dataState.result.size,
                        minDistance = 7,
                        minIndex = 3,
                        limit = 5)
                    insertAds(indices)
                    binding.srlArticles.isRefreshing = false

                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                    binding.srlArticles.isRefreshing = false
                }
            }
        })
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
        viewModel.totalVoteCountData.observe(viewLifecycleOwner, Observer { result ->
            if (result >= 0) {
                totalVoteCount = result
                articleAdapter.setPeriodComposition(viewModel.params.order, totalVoteCount)
            }
        })
        viewModel.isBookmarked.observe(viewLifecycleOwner, Observer { b ->
            if (b) {
                binding.ibBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
            } else {
                binding.ibBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
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

    private fun tapPositionToRequestOrder(tabPos: Int): Int {
        when(tabPos) {
            0 -> {
                return articleAdapter.FLAG_ALL
            }
            1 -> {
                return articleAdapter.FLAG_DAY
            }
            2 -> {
                return articleAdapter.FLAG_WEEK
            }
            3 -> {
                return articleAdapter.FLAG_MONTH
            }
            4 -> {
                return articleAdapter.FLAG_NEW
            }
            else -> {
                return 0
            }
        }
    }

    private fun insertAdsInArticleItems() {
        if (mNativeAds.size <= 0) {
            return
        }
        var index = 3
        if (articleAdapter.itemCount < 3 && articleAdapter.itemCount > 0) {
            index = articleAdapter.itemCount
        }
        val offset = 8
        for (ad in mNativeAds) {
            insertAd(index, ad)
            index = index + offset
        }
    }

    private fun insertAds(indices: List<Int>) {
        var i = 0
        for (index in indices) {
            try {
                insertAd(index, mNativeAds[i++])
            } catch (e: Exception) {

            }
        }
    }

    private fun insertAd(index: Int, ad: NativeAd) {
        if (index <= articleAdapter.itemCount) {
            articleAdapter.itemList.add(index, ad)
            articleAdapter.notifyItemInserted(index)
        }
    }

    private fun getIndexesOfList(listSize: Int, minDistance: Int, minIndex: Int, limit: Int): List<Int> {
        val indice = mutableListOf<Int>()
        var min = Math.round(listSize.toFloat()/limit)
        if (min < minIndex) {
            min = minIndex
        }
        var index = min
        while (index < listSize) {
            indice.add(index)
            index += minDistance
            if (indice.size >= limit) {
                break
            }
        }
        return indice
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
        adLoader = null
        super.onDestroy()
    }

}