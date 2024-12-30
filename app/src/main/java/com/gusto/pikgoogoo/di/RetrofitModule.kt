package com.gusto.pikgoogoo.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gusto.pikgoogoo.api.WebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder{
        val builded = Retrofit.Builder()
            .baseUrl("https://gusgusto.com/gg/")
            .addConverterFactory(GsonConverterFactory.create(gson))
        return builded
    }

    @Singleton
    @Provides
    fun provideWebService(retrofit: Retrofit.Builder): WebService {
        val ws = retrofit.build()
            .create(WebService::class.java)
        return ws
    }

}