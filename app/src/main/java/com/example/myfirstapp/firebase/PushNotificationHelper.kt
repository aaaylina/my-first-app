package com.example.myfirstapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myfirstapp.MainActivity
import com.example.myfirstapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    fun showDataPush(kind: String, title: String, message: String) {
        val channelId = channelIdForKind(kind)
        val (smallIcon, priority) = styleForKind(kind)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            kind.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(kind.hashCode(), notification)
    }

    private fun channelIdForKind(kind: String): String = when (kind) {
        KIND_PROMO -> CHANNEL_PROMO
        KIND_AUTH -> CHANNEL_AUTH
        else -> CHANNEL_DEFAULT
    }

    private fun styleForKind(kind: String): Pair<Int, Int> = when (kind) {
        KIND_PROMO -> R.drawable.ic_message to NotificationCompat.PRIORITY_HIGH
        KIND_AUTH -> R.drawable.ic_settings to NotificationCompat.PRIORITY_MAX
        else -> R.drawable.ic_edit to NotificationCompat.PRIORITY_DEFAULT
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channels = listOf(
            NotificationChannel(
                CHANNEL_PROMO,
                context.getString(R.string.notification_channel_promo),
                NotificationManager.IMPORTANCE_HIGH,
            ),
            NotificationChannel(
                CHANNEL_AUTH,
                context.getString(R.string.notification_channel_auth),
                NotificationManager.IMPORTANCE_HIGH,
            ),
            NotificationChannel(
                CHANNEL_DEFAULT,
                context.getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
        channels.forEach(notificationManager::createNotificationChannel)
    }

    companion object {
        const val KIND_PROMO = "promo"
        const val KIND_AUTH = "auth"

        const val DATA_KIND = "kind"
        const val DATA_TITLE = "title"
        const val DATA_MESSAGE = "message"

        private const val CHANNEL_PROMO = "push_promo"
        private const val CHANNEL_AUTH = "push_auth"
        private const val CHANNEL_DEFAULT = "push_default"
    }
}
