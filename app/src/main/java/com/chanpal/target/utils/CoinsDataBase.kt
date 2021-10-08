package com.chanpal.target.utils

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chanpal.target.application.TargetApplication
import com.chanpal.target.dao.CoinsDao
import com.chanpal.target.data.Coins

@Database(entities = [Coins::class], version = 1)
abstract class CoinsDataBase : RoomDatabase() {
    abstract fun getCoinsDao(): CoinsDao
    companion object {
        var instance = Single.SIN
    }
    private object Single {

        val SIN :CoinsDataBase= Room.databaseBuilder(
            TargetApplication.instance(),
            CoinsDataBase::class.java,
            "Coins.db"
        )
            .allowMainThreadQueries()
            .build()
    }
}