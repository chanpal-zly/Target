package com.chanpal.target.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chanpal.target.data.Coins

@Dao
interface CoinsDao:BaseDao<Coins> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: MutableList<Coins>)

    @Query("select * from Coins")
    fun getAllCoins():MutableList<Coins>

    @Query("select * from Coins where CoinsID = :CoinsID")
    fun getCoins(CoinsID:Int):Coins

    @Query("select * from Coins order by CoinsID desc ")
    fun getAllByDateDesc():MutableList<Coins>

    @Query("delete from Coins")
    fun deleteAll()
}