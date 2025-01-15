package com.gusto.pikgoogoo.datasource

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken

interface KakaoDataSource {
    suspend fun getKakaoToken(context: Context): OAuthToken
}