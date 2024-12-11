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
import com.zr.repository.item.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object NotificationHelper {
    private const val CHANNEL_ID = "NebulaChannelId"
    private const val CHANNEL_NAME = "Nebula Channel"
    private const val DELAY_TIME = 500L

    private lateinit var appContext: Context
    private var lastNotificationJob: Job? = null
    private var lastNotificationTime = 0L
    private var messageQueue = LimitedList<String>(5)

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
        messageQueue.addLimited(log.toString())
        if (System.currentTimeMillis() - lastNotificationTime > DELAY_TIME) {
            lastNotificationTime = System.currentTimeMillis()
            showNotification(log)
        } else {
            lastNotificationJob?.cancel()
            lastNotificationJob = CoroutineScope(Dispatchers.Main).launch {
                delay(DELAY_TIME)
                showNotification(log)
            }
        }
    }

    private fun showNotification(log: Log) {
        val notificationId = 1

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(appContext.getString(R.string.lib_name))
        messageQueue.forEach {
            inboxStyle.addLine(it)
        }
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(appContext.getString(R.string.lib_name))
            .setContentText(log.toString())
            .setContentIntent(pendingIntent)
            .setStyle(inboxStyle)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null)
            .build()

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationManager = NotificationManagerCompat.from(appContext)
            notificationManager.notify(notificationId, notification)
        }
    }

    private class LimitedList<T>(private val limitSize: Int) : ArrayList<T>() {
        fun addLimited(element: T): Boolean {
            if (size >= limitSize) removeAt(0)
            return super.add(element)
        }
    }
}