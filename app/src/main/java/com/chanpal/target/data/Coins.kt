package com.chanpal.target.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Coins")
data class Coins (
    @PrimaryKey(autoGenerate = true)
    var CoinsID: Int?,
    @ColumnInfo(name = "coins_date")
    var CoinsDate: Float?,
    @ColumnInfo(name = "coins_number")
    var CoinsNumber: Float?
)