package com.gusto.pikgoogoo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Grade
import com.gusto.pikgoogoo.util.GradeIconSelector

class GradeAdapter
constructor(
    val onClick: (Int) -> (Unit),
    val myGrade: Int = 0,
    val myGradeIcon: Int = -1
) : RecyclerView.Adapter<GradeAdapter.ViewHolder>() {

    private val itemList: MutableList<Grade> = mutableListOf()
    private val selector = GradeIconSelector()
    private var selectedId = 0

    fun setList(list: List<Grade>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ibGrade = view.findViewById(R.id.ib_grade) as ImageButton
        val ivSelected = view.findViewById(R.id.iv_selected) as ImageView
        val ivBlind = view.findViewById(R.id.iv_blind) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_grade, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList.get(position)
        selector.setImageViewGradeIcon(holder.ibGrade, item.id)
        if (myGradeIcon > 0) {
            if (item.id == myGradeIcon) {
                holder.ivSelected.visibility = View.VISIBLE
            } else {
                holder.ivSelected.visibility = View.GONE
            }
            var max = 0
            for (grade in itemList) {
                if (myGrade > grade.minValue) {
                    max = grade.id
                }
            }
            if (item.id > max) {
                holder.ivBlind.visibility = View.VISIBLE
            } else {
                holder.ivBlind.visibility = View.GONE
            }
        } else {
            if (selectedId == 0) {
                for (grade in itemList) {
                    if (myGrade > grade.minValue) {
                        selectedId = grade.id
                    }
                }
            }
            if (item.id == selectedId) {
                holder.ivSelected.visibility = View.VISIBLE
            } else {
                holder.ivSelected.visibility = View.GONE
                if (item.id > selectedId) {
                    holder.ivBlind.visibility = View.VISIBLE
                } else {
                    holder.ivBlind.visibility = View.GONE
                }
            }
        }
        holder.ibGrade.setOnClickListener {
            onClick(item.id)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}