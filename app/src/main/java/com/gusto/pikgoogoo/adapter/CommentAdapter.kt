package com.gusto.pikgoogoo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Article
import com.gusto.pikgoogoo.data.Comment
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.util.DateFormatManager
import com.gusto.pikgoogoo.util.GradeIconSelector
import com.gusto.pikgoogoo.util.LoadingViewHolder
import com.gusto.pikgoogoo.util.NumberFormatEditor

class CommentAdapter
constructor(
    val myId: Int,
    val onClick: (Int, Int, Int, Int) -> Unit,
    val onLongClick: (Int, Int, String, String, Int) -> Unit
) : ListAdapter<Comment, RecyclerView.ViewHolder>(CommentDiffCallback()){

    private val VIEW_TYPE_LOADING = -1
    private val VIEW_TYPE_GENERAL = 1
    private val VIEW_TYPE_MY_COMMENT = 2

    private lateinit var gradeList: List<Grade>
    private val selector: GradeIconSelector = GradeIconSelector()
    private val dateFormatManager = DateFormatManager()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            VIEW_TYPE_GENERAL -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
                return CommentHolder(view, selector, dateFormatManager, gradeList)
            }
            VIEW_TYPE_MY_COMMENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_comment, parent, false)
                return CommentHolder(view, selector, dateFormatManager, gradeList)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                return LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentHolder) {
            holder.bind(position, getItem(position), onClick, onLongClick)
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
            if (holder is CommentHolder) {
                if (payloads.get(0) == 1) {
                    getItem(position).likeCount += 1
                    holder.tvLikeCount.text = (getItem(position).likeCount).toString()
                } else if (payloads.get(0) is String) {
                    getItem(position).comment = payloads.get(0) as String
                    holder.tvComment.text = getItem(position).comment
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item.id > 0) {
            if (item.userId == myId) {
                return VIEW_TYPE_MY_COMMENT
            } else {
                return VIEW_TYPE_GENERAL
            }
        } else {
            return VIEW_TYPE_LOADING
        }
    }

    fun setGradeList(list: List<Grade>) {
        this.gradeList = list
    }

    fun setList(list: List<Comment>) {
        submitList(list)
        notifyDataSetChanged()
    }

    class CommentHolder(view: View, val selector: GradeIconSelector, val dateFormatManager: DateFormatManager, val gradeList: List<Grade>) : RecyclerView.ViewHolder(view) {
        val tvNickname: TextView
        val ibGrade: ImageButton
        val bArticle: Button
        val tvComment: TextView
        val tvDatetime: TextView
        val tvLikeCount: TextView
        val cvLike: CardView
        val cvComment: CardView
        val root: View
        val nfe: NumberFormatEditor
        init {
            tvNickname = view.findViewById(R.id.tv_nickname)
            ibGrade = view.findViewById(R.id.ib_grade)
            bArticle = view.findViewById(R.id.b_article)
            tvComment = view.findViewById(R.id.tv_comment)
            tvDatetime = view.findViewById(R.id.tv_datetime)
            tvLikeCount = view.findViewById(R.id.tv_like_count)
            cvLike = view.findViewById(R.id.cv_like)
            cvComment = view.findViewById(R.id.cv_comment)
            root = view.rootView
            nfe = NumberFormatEditor()
        }
        fun bind(position: Int, item: Comment, onClick: (Int, Int, Int, Int) -> Unit, onLongClick: (Int, Int, String, String, Int) -> Unit) {
            tvNickname.text = item.nickname
            if (item.content.equals("")) {
                bArticle.visibility = View.GONE
            } else {
                bArticle.visibility = View.VISIBLE
                bArticle.text = "@"+item.content
            }
            tvComment.text = item.comment
            tvDatetime.text = dateFormatManager.getTimeForm(item.createdDatetime)
            tvLikeCount.text = nfe.transfromNumber(item.likeCount.toDouble())
            cvLike.setOnClickListener {
                onClick(1, item.id, position, item.articleId)
            }
            if (item.gradeIcon > 0) {
                selector.setImageViewGradeIcon(ibGrade, item.gradeIcon)
            } else {
                selector.setImageViewGradeIcon(ibGrade, item.grade, gradeList)
            }
            bArticle.setOnClickListener {
                onClick(2, item.id, position, item.articleId)
            }
            root.setOnLongClickListener {
                onLongClick(position, item.id, item.comment, item.content, item.userId)
                return@setOnLongClickListener false
            }
        }
    }

}

private class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }

}