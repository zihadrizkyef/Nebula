package com.zr.nebula.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zr.nebula.data.item.Log

@Dao
internal interface LogDao {
    @Query("SELECT * FROM log ORDER BY createdAt DESC")
    fun getAll(): List<Log>

    @Query("SELECT * FROM log ORDER BY createdAt DESC")
    fun getAllLiveData(): LiveData<List<Log>>

    @Insert
    fun insertAll(vararg logs: Log)

    @Delete
    fun delete(log: Log)

    @Query("DELETE FROM log")
    fun deleteAll()
}