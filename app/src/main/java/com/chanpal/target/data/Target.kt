package com.chanpal.target.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Target")
data class Target(
    @PrimaryKey(autoGenerate = true)
    var TargetID: Int?,
    @ColumnInfo(name = "month_number")
    var MonthNumber: Float,
    @ColumnInfo(name = "target_progress")
    var TargetProgress: Float
)