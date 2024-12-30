package com.gusto.pikgoogoo.data.repository

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.util.DataState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdRepository
@Inject
constructor() {

    lateinit var adLoader: AdLoader

    suspend fun getAds(adNum:Int, context: Context) = flow {
        emit(DataState.Loading("광고를 가져오는 중입니다"))
        val adsChannel = Channel<NativeAd>()
        emit(suspendCoroutine { continuation ->
            val builder = AdLoader.Builder(context, context.getString(R.string.ad_unit_id))
            adLoader = builder.forNativeAd { nativeAd ->
                if (adLoader.isLoading) {
                    // The AdLoader is still loading ads.
                    // Expect more adLoaded or onAdFailedToLoad callbacks.
                } else {
                    // The AdLoader has finished loading ads.
                    continuation.resume(DataState.Success(nativeAd))
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure.
                    continuation.resume(DataState.Error(Exception(adError.message)))
                }

            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()
            adLoader.loadAds(AdRequest.Builder().build(), adNum)
        })
    }

}