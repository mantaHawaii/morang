package com.gusto.pikgoogoo.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.gusto.pikgoogoo.R


class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val nativeAdView: NativeAdView

    init {
        nativeAdView = view.findViewById(R.id.ad_view)
        nativeAdView.mediaView = view.findViewById(R.id.ad_media)

        nativeAdView.headlineView = view.findViewById(R.id.ad_headline)
        nativeAdView.bodyView = view.findViewById(R.id.ad_body)
        nativeAdView.callToActionView = view.findViewById(R.id.ad_call_to_action)
        nativeAdView.iconView = view.findViewById(R.id.ad_icon)
        nativeAdView.priceView = view.findViewById(R.id.ad_price)
        nativeAdView.starRatingView = view.findViewById(R.id.ad_stars)
        nativeAdView.storeView = view.findViewById(R.id.ad_store)
        nativeAdView.advertiserView = view.findViewById(R.id.ad_advertiser)

    }

    fun getAdView(): NativeAdView {
        return nativeAdView
    }

}