package com.zr.nebula

import android.content.Context
import com.zr.repository.DbHelper
import com.zr.nebula.helper.NotificationHelper
import com.zr.repository.item.Level
import com.zr.repository.item.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Nebula {
    private lateinit var appContext: Context
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        if (::appContext.isInitialized) return

        this.appContext = context.applicationContext
        NotificationHelper.init(appContext)
        DbHelper.init(appContext)
    }

    fun i(message: String) = insert(Level.INFO, message)
    fun d(message: String) = insert(Level.DEBUG, message)
    fun w(message: String) = insert(Level.WARN, message)
    fun e(message: String) = insert(Level.ERROR, message)

    private fun insert(level: Level, message: String) {
        if (::appContext.isInitialized.not())
            throw IllegalStateException("Nebula not initialized. Please call `Nebula.init()` first.")

        val log = Log().apply {
            this.levelCode = level.code
            this.message = message
        }
        ioScope.launch {
            DbHelper.insert(log)
        }
        NotificationHelper.scheduleNotification(log)
    }
}