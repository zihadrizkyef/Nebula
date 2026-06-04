package com.zr.nebula.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zr.nebula.data.DbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ClearLogsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationHelper.ACTION_CLEAR_ALL) return

        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            DbHelper.init(appContext)
            DbHelper.deleteAll()
            NotificationHelper.init(appContext)
            NotificationHelper.clear()
            pendingResult.finish()
        }
    }
}