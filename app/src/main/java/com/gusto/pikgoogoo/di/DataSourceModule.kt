package com.gusto.pikgoogoo.di

import android.content.Context
import com.gusto.pikgoogoo.datasource.FirebaseDataSource
import com.gusto.pikgoogoo.datasource.FirebaseDataSourceImpl
import com.gusto.pikgoogoo.datasource.GoogleDataSource
import com.gusto.pikgoogoo.datasource.GoogleDataSourceImpl
import com.gusto.pikgoogoo.datasource.KakaoDataSource
import com.gusto.pikgoogoo.datasource.KakaoDataSourceImpl
import com.gusto.pikgoogoo.datasource.PreferenceDataSource
import com.gusto.pikgoogoo.datasource.PreferenceDataSourceImpl
import com.gusto.pikgoogoo.util.LoginManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideFirebaseDatasource() : FirebaseDataSource {
        return FirebaseDataSourceImpl()
    }

    @Singleton
    @Provides
    fun provideGoogleDatasource() : GoogleDataSource {
        return GoogleDataSourceImpl()
    }

    @Singleton
    @Provides
    fun provideKakaoDatasource() : KakaoDataSource {
        return KakaoDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providePreferenceDatasource() : PreferenceDataSource {
        return PreferenceDataSourceImpl()
    }

}