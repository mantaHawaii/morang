package com.gusto.pikgoogoo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gusto.pikgoogoo.data.Grade

@Dao
interface GradeDao {

    @Query("SELECT * FROM grade ORDER BY id ASC")
    fun getGradeAll(): List<Grade>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGradeAll(vararg grade: Grade)


}