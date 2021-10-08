package com.chanpal.target.utils

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chanpal.target.application.TargetApplication
import com.chanpal.target.dao.TargetDao
import com.chanpal.target.data.Target

@Database(entities = [Target::class], version = 1)
abstract class TargetDataBase : RoomDatabase() {
    abstract fun getTargetDao(): TargetDao
    companion object {
        var instance = Single.sin
    }
    private object Single {

        val sin :TargetDataBase= Room.databaseBuilder(
            TargetApplication.instance(),
            TargetDataBase::class.java,
            "Target.db"
        )
            .allowMainThreadQueries()
            .build()
    }
}