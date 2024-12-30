package com.gusto.pikgoogoo.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Article
import com.gusto.pikgoogoo.util.AdViewHolder
import com.gusto.pikgoogoo.util.GlideLoader
import com.gusto.pikgoogoo.util.LoadingViewHolder
import com.gusto.pikgoogoo.util.NumberFormatEditor
import kotlinx.coroutines.tasks.await

class ArticleAdapter
constructor(
    val context: Context,
    val itemList: MutableList<Any>,
    val onClick: (Int, Int, Int, String) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val VIEW_TYPE_LOADING = -1
    private val VIEW_TYPE_AD = 0
    private val VIEW_TYPE_GENERAL = 1

    var totalVoteCount: Int = 0

    val FLAG_MONTH = 30
    val FLAG_ALL = 0
    val FLAG_DAY = 1
    val FLAG_WEEK = 7
    val FLAG_NEW = 101

    var flagFor = FLAG_ALL
    private val nfe = NumberFormatEditor()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            VIEW_TYPE_GENERAL -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
                return ArticleHolder(view, nfe)
            }
            VIEW_TYPE_AD -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.native_ad_layout, parent, false)
                return AdViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                return LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            VIEW_TYPE_GENERAL -> {

                val item = itemList.get(position)

                if (holder is ArticleHolder) {

                    if (item is Article) {

                        holder.bind(position, item, onClick)
                        when (flagFor) {
                            FLAG_NEW -> {
                                var rate = 0.0
                                if (totalVoteCount != 0) {
                                    rate =
                                        ((item.voteCount).toDouble() / totalVoteCount.toDouble()) * 100.0
                                }
                                holder.tvVoteRate.text = nfe.transfromNumber(rate)
                                holder.pbVote.progress = rate.toInt()
                            }
                            else -> {
                                var rate = 0.0
                                if (totalVoteCount != 0) {
                                    rate =
                                        ((item.voteCount).toDouble() / totalVoteCount.toDouble()) * 100.0
                                }
                                holder.tvVoteRate.text = nfe.transfromNumber(rate)
                                holder.pbVote.progress = rate.toInt()
                            }
                        }
                    }
                }

            }
            VIEW_TYPE_AD -> {

                val item = itemList.get(position)

                if (holder is AdViewHolder) {
                    if (item is NativeAd) {
                        populateNativeAdView(item, holder.getAdView())
                    }
                }

            }
            else -> {

            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (holder is ArticleHolder) {
                val viewOne = holder.root.findViewById<TextView>(R.id.tv_one)
                val item = itemList.get(position) as Article
                viewOne.visibility = View.VISIBLE
                ValueAnimator.ofFloat(0f, 100f).apply {
                    duration = 1000
                    addUpdateListener { updatedAnimation ->
                        var currentValue = updatedAnimation.animatedValue as Float
                        viewOne.translationY = -currentValue*2.75f
                        viewOne.alpha = 1.0f-(currentValue/100.0f)
                        if (viewOne.alpha == 0f) {
                            viewOne.visibility = View.INVISIBLE
                        }
                    }
                    start()
                }
                item.voteCount += 1
                item.voteCountFor += 1
                holder.tvVoteCount.text = item.voteCount.toString()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = itemList.get(position)
        if (item is Article) {
            if (item.id > 0) {
                return VIEW_TYPE_GENERAL
            } else {
                return VIEW_TYPE_LOADING
            }
        } else {
            return VIEW_TYPE_AD
        }
    }

    fun setList(list: List<Article>) {
        itemList.clear()
        itemList.addAll(list)
        determineTheRanking()
        notifyDataSetChanged()
    }

    fun prepareAdSlot(indices: List<Int>) {
        for (index in indices) {
            if (itemCount > index) {
                itemList.add(index, )
            }
        }
    }

    fun bindLoadingItem() {
        itemList.add(Article(0, 0, "", 0, "",0 ,0, "", 0, 0))
    }

    fun removeLoadingItem() {
        var cnt = 0
        for (i in itemList) {
            if (i is Article && i.id == 0) {
                itemList.removeAt(cnt)
            }
            cnt++
        }
    }

    fun clearAds() {
        val iterator = itemList.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item is NativeAd) {
                item.destroy()
                iterator.remove()
            }
        }
    }

    fun setPeriodComposition(flag: Int, voteCount: Int){
        flagFor = flag
        totalVoteCount = voteCount
    }

    private fun determineTheRanking() {
        var rank = 1
        var preVoteCount = -1
        var preRank = 0
        if (flagFor != FLAG_NEW) {
            for (item in itemList) {
                if (item is Article) {
                    if (item.voteCount < preVoteCount) {
                        item.rank = rank
                    } else if (item.voteCount == preVoteCount) {
                        item.rank = preRank
                    } else {
                        item.rank = rank
                    }
                    preRank = item.rank
                    preVoteCount = item.voteCount
                    rank++
                }
            }
        } else {
            for (item in itemList) {
                if (item is Article) {
                    item.rank = -1
                }
            }
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        (adView.headlineView as TextView).setText(nativeAd.headline)
        (adView.bodyView as TextView).setText(nativeAd.body)
        (adView.callToActionView as Button).setText(nativeAd.callToAction)
        val icon = nativeAd.icon

        if (icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView?)!!.setImageDrawable(icon.drawable)
            adView.iconView!!.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView?)!!.text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView?)!!.text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar)
                .setRating(nativeAd.starRating!!.toFloat())
            adView.starRatingView!!.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView?)!!.text = nativeAd.advertiser
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        // Assign native ad object to the native view.

        adView.setNativeAd(nativeAd)
    }

    class ArticleHolder(view: View, val nfe: NumberFormatEditor) : RecyclerView.ViewHolder(view) {

        val tvRank: TextView
        val ivArticle: ImageView
        val cvImage: CardView
        val tvContent: TextView
        val pbVote: ProgressBar
        val tvVoteRate: TextView
        val tvCommentCount: TextView
        val tvVoteCount: TextView
        val cvVote: CardView
        val cvComment: CardView
        val root: View

        private val resizedSize = "_400x400"
        private val fileExtension = ".jpg"

        init {
            tvRank = view.findViewById(R.id.tv_rank)
            ivArticle = view.findViewById(R.id.iv_article)
            cvImage = view.findViewById(R.id.cv_image)
            tvContent = view.findViewById(R.id.tv_content)
            pbVote = view.findViewById(R.id.pb_vote)
            tvVoteRate = view.findViewById(R.id.tv_vote_rate)
            tvCommentCount = view.findViewById(R.id.tv_comment_count)
            tvVoteCount = view.findViewById(R.id.tv_vote_count)
            cvVote = view.findViewById(R.id.cv_vote)
            cvComment = view.findViewById(R.id.cv_comment)
            root = view.rootView
        }

        fun bind(pos: Int, item: Article, onClick: (Int, Int, Int, String) -> Unit) {

            cvImage.layoutParams.width = cvImage.layoutParams.height
            (tvContent.layoutParams as ViewGroup.MarginLayoutParams).marginStart = dpToPx(root.context, 24f).toInt()
            ivArticle.visibility = View.VISIBLE

            ivArticle.setImageResource(R.color.background_grey)
            if (item.rank > 0) {
                tvRank.text = item.rank.toString()
            } else {
                tvRank.text = ""
            }
            tvContent.text = item.content
            tvCommentCount.text = nfe.transfromNumber(item.commentCount)
            tvVoteCount.text = nfe.transfromNumber(item.voteCount)

            val loader = GlideLoader()
            val onFailure = {

            }
            val onSuccess = {

            }
            if (item.imageUri != null) {
                loader.loadImage(ivArticle,
                    item.imageUri!!,
                    if (item.cropImage == 0) false else true,
                    onFailure,
                    onSuccess
                )
            } else {
                setLayoutInfo()
            }


            cvVote.setOnClickListener {
                onClick(0, pos, item.id, item.content)
            }

            cvComment.setOnClickListener {
                onClick(1, pos, item.id, item.content)
            }

            root.setOnClickListener {
                onClick(2, pos, item.id, item.content)
            }

        }

        private fun setLayoutInfo() {
            cvImage.layoutParams.width = 1
            tvContent.updateLayoutParams {
                (this as ViewGroup.MarginLayoutParams).marginStart = 0
            }
            ivArticle.visibility = View.INVISIBLE
        }

        private fun dpToPx(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}

data class LcItem(val id: Int, var count: Int)

private class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }

}