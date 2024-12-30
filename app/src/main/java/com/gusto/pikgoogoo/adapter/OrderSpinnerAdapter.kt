package com.gusto.pikgoogoo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Category

class OrderSpinnerAdapter
constructor(
    private val context: Context,
    private val list: Array<String>
) : BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_order_spinner, parent, false)
        view.findViewById<TextView>(R.id.tv_order).text = list.get(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_order_spinner_dropdown, parent, false)
        view.findViewById<TextView>(R.id.tv_order).text = list.get(position)
        return view
    }
}