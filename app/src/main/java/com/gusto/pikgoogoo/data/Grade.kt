package com.gusto.pikgoogoo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grade")
data class Grade(
    @PrimaryKey val id: Int,
    var minValue: Int
)
