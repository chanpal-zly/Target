package com.chanpal.target.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chanpal.target.data.Target

@Dao
interface TargetDao:BaseDao<Target> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: Target)

    @Query("select * from Target")
    fun getAllTarget():MutableList<Target>

    @Query("select * from Target where TargetID = :TargetID")
    fun getTarget(TargetID:Int):Target

    @Query("select * from Target order by TargetID desc ")
    fun getAllByDateDesc():MutableList<Target>

    @Query("delete from Target")
    fun deleteAll()
}