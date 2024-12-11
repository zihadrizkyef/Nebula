package com.zr.repository

import android.content.Context
import androidx.room.Room
import com.zr.repository.item.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object DbHelper {
    private lateinit var logDao: LogDao

    fun init(context: Context) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nebula-db"
        ).build()
        logDao = db.logDao()
    }

    suspend fun getAll(): List<Log> = withContext(Dispatchers.IO) {
        return@withContext logDao.getAll()
    }

    suspend fun insert(log: Log) = withContext(Dispatchers.IO) {
        logDao.insertAll(log)
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        logDao.deleteAll()
    }

    fun setLogsListener(throttleTime: Long = 500, onChanged: (List<Log>) -> Unit) {
        var lastListenerTime = 0L
        var lastListenerJob: Job? = null
        val scope = CoroutineScope(Dispatchers.Main)
        logDao.getAllLiveData().observeForever {
            if (System.currentTimeMillis() - lastListenerTime > throttleTime) {
                lastListenerTime = System.currentTimeMillis()
                onChanged(it)
            } else {
                lastListenerJob?.cancel()
                lastListenerJob = scope.launch {
                    delay(500)
                    onChanged(it)
                }
            }
        }
    }
}