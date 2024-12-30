package com.gusto.pikgoogoo.di

import android.content.Context
import androidx.room.Room
import com.gusto.pikgoogoo.data.AppDatabase
import com.gusto.pikgoogoo.data.dao.GradeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideLocalDb(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesGradeDao(appDatabase: AppDatabase): GradeDao {
        return appDatabase.gradeDao()
    }

}