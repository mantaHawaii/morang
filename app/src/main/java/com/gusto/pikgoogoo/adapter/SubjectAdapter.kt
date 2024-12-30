package com.gusto.pikgoogoo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.data.Subject
import com.gusto.pikgoogoo.util.GradeIconSelector
import com.gusto.pikgoogoo.util.LoadingViewHolder
import com.gusto.pikgoogoo.util.NumberFormatEditor

class SubjectAdapter
constructor(
    val onClickItem: (Subject, Int) -> Unit,
    val onClickCategory: (Int) -> Unit
): ListAdapter<Subject, RecyclerView.ViewHolder>(SubjectDiffCallback()) {

    private val VIEW_TYPE_LOADING = -1
    private val VIEW_TYPE_GENERAL = 1

    private val selector = GradeIconSelector()
    private lateinit var gradeList: List<Grade>

    private val nfe: NumberFormatEditor = NumberFormatEditor()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            VIEW_TYPE_GENERAL -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
                return SubjectHolder(view, selector, nfe, gradeList)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                return LoadingViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).id > 0) {
            return VIEW_TYPE_GENERAL
        } else {
            return VIEW_TYPE_LOADING
        }
    }

    fun setGradeList(list: List<Grade>) {
        this.gradeList = list
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SubjectHolder) {
            holder.bind(position, getItem(position), onClickItem, onClickCategory)
        }
    }

    class SubjectHolder(view: View, iconSelector: GradeIconSelector, val nfe: NumberFormatEditor, val gradeList: List<Grade>) : RecyclerView.ViewHolder(view) {

        val tvTitle: TextView
        val tvNickname: TextView
        val ivGrade: ImageView
        val tvVoteCount: TextView
        val tvCommentCount: TextView
        val bCategory: Button
        val root: View
        private val selector = iconSelector

        init {
            tvTitle = view.findViewById(R.id.tv_title)
            tvNickname = view.findViewById(R.id.tv_nickname)
            ivGrade = view.findViewById(R.id.iv_grade)
            tvVoteCount = view.findViewById(R.id.tv_vote_count)
            tvCommentCount = view.findViewById(R.id.tv_comment_count)
            bCategory = view.findViewById(R.id.b_category)
            ivGrade.clipToOutline = true
            root = view.rootView
        }

        fun bind(position: Int, item: Subject, onClickItem: (Subject, Int) -> Unit, onClickCategory: (Int) -> Unit) {
            tvTitle.text = item.title
            tvNickname.text = item.userNicknmae
            tvVoteCount.text = nfe.transfromNumber(item.voteCount)
            tvCommentCount.text = nfe.transfromNumber(item.commentCount)
            bCategory.text = item.categoryName
            if (item.userGradeIcon > 0) {
                selector.setImageViewGradeIcon(ivGrade, item.userGradeIcon)
            } else {
                selector.setImageViewGradeIcon(ivGrade, item.userGrade, gradeList)
            }
            bCategory.setOnClickListener {
                onClickCategory(item.categoryId)
            }

            root.setOnClickListener {
                onClickItem(item, position)
            }

        }

    }

}

private class SubjectDiffCallback : DiffUtil.ItemCallback<Subject>() {

    override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem == newItem
    }

}
