package com.zr.nebula

import android.content.Context
import com.zr.nebula.helper.DbHelper
import com.zr.nebula.helper.NotificationHelper
import com.zr.nebula.item.Level
import com.zr.nebula.item.Log
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Nebula {
    private lateinit var appContext: Context

    private val realmScope = CoroutineScope(Dispatchers.IO)

    fun i(message: String) = insert(Level.INFO, message)
    fun d(message: String) = insert(Level.DEBUG, message)
    fun w(message: String) = insert(Level.WARN, message)
    fun e(message: String) = insert(Level.ERROR, message)

    private fun insert(level: Level, message: String) {
        val log = Log().apply {
            this.levelCode = level.code
            this.message = message
        }
        realmScope.launch {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction {
                    it.insert(log)
                }
            }
        }
        NotificationHelper.scheduleNotification(log)
    }

    fun init(context: Context) {
        this.appContext = context.applicationContext
        NotificationHelper.init(appContext)
        DbHelper.init(appContext)
    }
}