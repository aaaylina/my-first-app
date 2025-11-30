package com.example.myfirstapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.myfirstapp.MainActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.model.NotificationData
import com.example.myfirstapp.model.NotificationPriority
import com.example.myfirstapp.navigation.NotificationKeys
import com.example.myfirstapp.receiver.ReplyReceiver

class NotificationHandler(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_HIGH,
                    context.getString(R.string.priority_high),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.high_priority_description)
                },
                NotificationChannel(
                    CHANNEL_DEFAULT,
                    context.getString(R.string.priority_medium),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.medium_priority_description)
                },
                NotificationChannel(
                    CHANNEL_LOW,
                    context.getString(R.string.priority_low),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = context.getString(R.string.low_priority_description)
                },
                NotificationChannel(
                    CHANNEL_MIN,
                    context.getString(R.string.priority_min),
                    NotificationManager.IMPORTANCE_MIN
                ).apply {
                    description = context.getString(R.string.min_priority_description)
                }
            )
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun showNotification(data: NotificationData) {
        val channelId = when (data.priority) {
            NotificationPriority.MIN -> CHANNEL_MIN
            NotificationPriority.LOW -> CHANNEL_LOW
            NotificationPriority.MEDIUM -> CHANNEL_DEFAULT
            NotificationPriority.HIGH -> CHANNEL_HIGH
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_back_hand_24)
            .setContentTitle(data.title)
            .setPriority(getPriorityCompat(data.priority))
            .setAutoCancel(true)

        if (data.message.isNotEmpty()) {
            if (data.isExpandable && data.message.length > 50) {
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(data.message))
                builder.setContentText(data.message.take(50) + "...")
            } else {
                builder.setContentText(data.message)
            }
        }

        if (data.shouldOpenApp) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra(NotificationKeys.NOTIFICATION_TITLE, data.title)
                putExtra(NotificationKeys.NOTIFICATION_MESSAGE, data.message)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                data.notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
        }

        if (data.hasReplyAction) {
            val remoteInput = RemoteInput.Builder(ReplyReceiver.KEY_REPLY)
                .setLabel(context.getString(R.string.reply_hint))
                .build()

            val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
                putExtra(ReplyReceiver.KEY_NOTIFICATION_ID, data.notificationId)
            }

            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                data.notificationId,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val action = NotificationCompat.Action.Builder(
                R.drawable.ic_back_hand_24,
                context.getString(R.string.reply_label),
                replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            builder.addAction(action)
        }

        notificationManager.notify(data.notificationId, builder.build())
    }

    fun updateNotification(notificationId: Int, newText: String): Boolean {
        val activeNotifications = notificationManager.activeNotifications
        val notificationExists = activeNotifications.any { it.id == notificationId }

        if (notificationExists) {
            val data = NotificationData(
                title = context.getString(R.string.updated_notification_title),
                message = newText,
                notificationId = notificationId
            )
            showNotification(data)
            return true
        }
        return false
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    fun hasActiveNotifications(): Boolean {
        return notificationManager.activeNotifications.isNotEmpty()
    }

    private fun getPriorityCompat(priority: NotificationPriority): Int {
        return when (priority) {
            NotificationPriority.MIN -> NotificationCompat.PRIORITY_MIN
            NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationPriority.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
        }
    }

    companion object {
        const val CHANNEL_HIGH = "channel_high"
        const val CHANNEL_DEFAULT = "channel_default"
        const val CHANNEL_LOW = "channel_low"
        const val CHANNEL_MIN = "channel_min"
    }
}