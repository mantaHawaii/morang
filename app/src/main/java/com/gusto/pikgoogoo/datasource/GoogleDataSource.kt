package com.gusto.pikgoogoo.datasource

import android.content.Context
import androidx.credentials.Credential

interface GoogleDataSource {
    suspend fun getGoogleIdTokenCredential(credential: Credential): String
    suspend fun getCredential(context: Context): Credential
}