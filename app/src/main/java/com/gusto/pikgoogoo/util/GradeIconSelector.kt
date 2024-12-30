package com.gusto.pikgoogoo.util

import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Grade

class GradeIconSelector {

    private val baseUrl = "https://gusgusto.com/gg/grade/icons/"

    fun setImageViewGradeIcon(iv: ImageView, gradeIcon: Int) {

        val gm = Glide.with(iv.context)

        var url = baseUrl+"1.png"

        if (gradeIcon > 0) {
            url = baseUrl+gradeIcon.toString()+".png"
        }

        gm.load(url).fitCenter().transition(DrawableTransitionOptions.withCrossFade()).into(iv)

    }

    fun setImageViewGradeIcon(iv: ImageView, gradeValue: Int, gradeList: List<Grade>) {

        val gm = Glide.with(iv.context)
        var url = baseUrl

        var cnt = 0
        var gradeIcon: Int = 1

        for (grade in gradeList) {
            if (gradeValue > grade.minValue) {
                gradeIcon = grade.id
            }
        }

        url = baseUrl+gradeIcon.toString()+".png"

        gm.load(url).fitCenter().transition(DrawableTransitionOptions.withCrossFade()).into(iv)

    }
}