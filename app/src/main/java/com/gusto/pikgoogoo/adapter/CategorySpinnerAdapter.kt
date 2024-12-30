package com.gusto.pikgoogoo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Category

class CategorySpinnerAdapter(
    private val context: Context
) : BaseAdapter() {

    val itemList = mutableListOf<Category>()

    fun setList(items: List<Category>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_category_spinner, parent, false)
        view.findViewById<TextView>(R.id.tv_category).text = itemList.get(position).name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_category_spinner, parent, false)
        view.findViewById<TextView>(R.id.tv_category).text = itemList.get(position).name
        return view
    }
}