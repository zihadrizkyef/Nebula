package com.zr.nebula.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zr.nebula.data.item.Log

@Database(entities = [Log::class], version = 1)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}