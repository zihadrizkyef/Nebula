package com.zr.nebula.helper

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zr.nebula.R
import com.zr.nebula.activity.MainActivity
import com.zr.nebula.data.item.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedDeque

internal object NotificationHelper {
    private const val NOTIF_ID = 1
    private const val CHANNEL_ID = "NebulaChannelId"
    private const val CHANNEL_NAME = "Nebula Channel"
    private const val DELAY_TIME = 500L
    private const val CLEAR_REQUEST_CODE = 1
    internal const val ACTION_CLEAR_ALL = "com.zr.nebula.action.CLEAR_ALL"

    private lateinit var appContext: Context
    private var lastNotificationJob: Job? = null
    private var lastNotificationTime = 0L
    private var messageQueue = ConcurrentLinkedDeque<String>()
    private const val MESSAGE_LIMIT = 5
    private const val MESSAGE_CHARACTER_LIMIT = 80

    fun init(context: Context) {
        appContext = context.applicationContext

        val description = "Showing notification when log is added"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance)
            .setName(CHANNEL_NAME)
            .setSound(null, null)
            .setDescription(description)
            .build()
        val notificationManager = NotificationManagerCompat.from(appContext)
        notificationManager.createNotificationChannel(channel)
    }

    fun scheduleNotification(log: Log) {
        addMessage(log.toString())
        if (System.currentTimeMillis() - lastNotificationTime > DELAY_TIME) {
            lastNotificationTime = System.currentTimeMillis()
            showNotification(log)
        } else {
            lastNotificationJob?.cancel()
            lastNotificationJob = CoroutineScope(Dispatchers.IO).launch {
                delay(DELAY_TIME)
                showNotification(log)
            }
        }
    }

    private fun showNotification(log: Log) {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val clearIntent = Intent(appContext, ClearLogsReceiver::class.java).apply {
            action = ACTION_CLEAR_ALL
        }
        val clearPendingIntent = PendingIntent.getBroadcast(
            appContext,
            CLEAR_REQUEST_CODE,
            clearIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(appContext.getString(R.string.lib_name))
        messageQueue.descendingIterator().forEach {
            inboxStyle.addLine(it)
        }
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_info_24)
            .setContentTitle(appContext.getString(R.string.lib_name))
            .setContentText(log.toString())
            .setContentIntent(pendingIntent)
            .setStyle(inboxStyle)
            .setAutoCancel(true)
            .addAction(
                R.drawable.baseline_delete_sweep_24,
                appContext.getString(R.string.clear),
                clearPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null)
            .build()

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationManager = NotificationManagerCompat.from(appContext)
            notificationManager.notify(NOTIF_ID, notification)
        }
    }

    private fun addMessage(message: String) {
        if (messageQueue.size >= MESSAGE_LIMIT) {
            messageQueue.pollFirst() // remove oldest
        }
        val displayMessage = if (message.length > MESSAGE_CHARACTER_LIMIT) {
            message.take(MESSAGE_CHARACTER_LIMIT - 1) + "…"
        } else {
            message
        }
        messageQueue.offerLast(displayMessage) // add newest
    }

    fun clear() {
        messageQueue.clear()
        val notificationManager = NotificationManagerCompat.from(appContext)
        notificationManager.cancel(NOTIF_ID)
    }
}
