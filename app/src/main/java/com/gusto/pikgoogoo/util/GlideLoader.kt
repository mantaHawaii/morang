package com.gusto.pikgoogoo.util

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import javax.inject.Inject

class GlideLoader
@Inject
constructor() {

    fun loadImage(iv:ImageView, uri: Uri, crop: Boolean, onFailure: ()->Unit, onSuccess: ()->Unit) {
        var builder = Glide
            .with(iv.context)
            .load(uri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFailure()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccess()
                    return false
                }

            })
            .transition(DrawableTransitionOptions.withCrossFade())

        if (crop) {
            builder = builder.centerCrop()
        } else {
            builder = builder.fitCenter()
        }

        builder.into(iv)
    }

    fun loadThumbnail(iv: ImageView, uri: Uri, crop:Boolean, onFailure: ()->Unit, onSuccess: ()->Unit) {

        var builder = Glide
            .with(iv.context)
            .load(uri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFailure()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccess()
                    return false
                }

            })
            .override(400, 400)
            .transition(DrawableTransitionOptions.withCrossFade())

        if (crop) {
            builder = builder.centerCrop()
        } else {
            builder = builder.fitCenter()
        }

        builder.into(iv)
    }

}