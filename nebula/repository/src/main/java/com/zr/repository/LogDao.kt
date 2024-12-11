package com.zr.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zr.repository.item.Log

@Dao
interface LogDao {
    @Query("SELECT * FROM log ORDER BY createdAt DESC")
    fun getAll(): List<Log>

    @Query("SELECT * FROM log ORDER BY createdAt DESC")
    fun getAllLiveData(): androidx.lifecycle.LiveData<List<Log>>

    @Insert
    fun insertAll(vararg logs: Log)

    @Delete
    fun delete(log: Log)

    @Query("DELETE FROM log")
    fun deleteAll()
}