package com.gusto.pikgoogoo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gusto.pikgoogoo.data.dao.GradeDao

@Database(entities = [Grade::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gradeDao(): GradeDao

    companion object{
        val DATABASE_NAME: String = "morang.db"
    }

}