package com.gusto.pikgoogoo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Category

class CategoryAdapter
constructor(
    val list: MutableList<Category>,
    val onClick: (Int) -> (Unit)
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvCategory = view.findViewById<TextView>(R.id.tv_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_simple_text, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list.get(position)
        holder.tvCategory.setText(item.name)
        holder.itemView.setOnClickListener {
            onClick(item.id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}