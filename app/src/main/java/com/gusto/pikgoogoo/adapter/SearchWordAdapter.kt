package com.gusto.pikgoogoo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gusto.pikgoogoo.R

class SearchWordAdapter
constructor(
    private val list: ArrayList<String>,
    val onClick: (Int, Int, String) -> (Unit)
) : RecyclerView.Adapter<SearchWordAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvSearchWord: TextView = view.findViewById(R.id.tv_searchword) as TextView
        var ibDelete: ImageButton = view.findViewById(R.id.ib_delete) as ImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_searchword, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvSearchWord.text = list.get(position)
        holder.itemView.setOnClickListener {
            onClick(0, position, list.get(position))
        }
        holder.ibDelete.setOnClickListener {
            onClick(1, position, list.get(position))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}