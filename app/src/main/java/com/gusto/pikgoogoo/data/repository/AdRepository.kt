package com.gusto.pikgoogoo.data.repository

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AdRepository
@Inject
constructor() : ParentRepository() {

    fun fetchAds(context: Context, numberOfAds: Int) = callbackFlow {
        trySend(DataState.Loading("광고 불러오는 중"))
        var adsCount = 0
        val builder = AdLoader.Builder(context, context.getString(R.string.ad_unit_id))
        var adLoader: AdLoader? = builder.forNativeAd { nativeAd ->
            trySend(DataState.Success(nativeAd))
            adsCount++
            if (adsCount == numberOfAds) {
                close()
            }
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: LoadAdError) {
                trySend(DataState.Error(Exception(errorCode.message)))
                close()
            }
        }).build()
        adLoader?.loadAds(AdRequest.Builder().build(), numberOfAds)
        awaitClose {
            adLoader = null
        }
    }.flowOn(Dispatchers.IO)

}