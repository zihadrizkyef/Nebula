package com.zr.nebula

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.zr.nebula.activity.MainActivity
import com.zr.nebula.data.DbHelper
import com.zr.nebula.helper.NotificationHelper
import com.zr.nebula.data.item.Level
import com.zr.nebula.data.item.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java

object Nebula {
    private lateinit var appContext: Context
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        if (::appContext.isInitialized) return

        this.appContext = context.applicationContext
        NotificationHelper.init(appContext)
        DbHelper.init(appContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)

            val shortcut = ShortcutInfo.Builder(context, "open_nebula")
                .setShortLabel("Open Nebula")
                .setLongLabel("Open Nebula")
                .setIcon(Icon.createWithResource(context, R.drawable.shortcut_icon))
                .setIntent(
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                )
                .build()

            shortcutManager?.addDynamicShortcuts(listOf(shortcut))
        }
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