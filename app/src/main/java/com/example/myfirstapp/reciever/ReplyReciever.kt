package com.example.myfirstapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput
import com.example.myfirstapp.R
import com.example.myfirstapp.utils.MessageRepository

class ReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val replyText = remoteInput?.getCharSequence(KEY_REPLY)?.toString()

        replyText?.let { text ->
            if (text.isNotEmpty()) {
                MessageRepository.addMessage(text, true)
                val notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, -1)
                if (notificationId != -1) {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                    notificationManager.cancel(notificationId)
                }
                Toast.makeText(context, context.getString(R.string.reply_saved, text), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val KEY_REPLY = "key_reply"
        const val KEY_NOTIFICATION_ID = "key_notification_id"
    }
}