package com.gusto.pikgoogoo.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.gusto.pikgoogoo.util.LoginManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Singleton
    @Provides
    fun provideLoginManager(@ApplicationContext context: Context) : LoginManager {
        return LoginManager(context)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}