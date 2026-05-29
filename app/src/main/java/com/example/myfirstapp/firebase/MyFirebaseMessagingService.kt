package com.example.myfirstapp.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushNotificationHelper: PushNotificationHelper

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        if (data.isEmpty()) return

        val kind = data[PushNotificationHelper.DATA_KIND] ?: return
        val title = data[PushNotificationHelper.DATA_TITLE].orEmpty()
            .ifBlank { getString(com.example.myfirstapp.R.string.push_default_title) }
        val body = data[PushNotificationHelper.DATA_MESSAGE].orEmpty()
            .ifBlank { getString(com.example.myfirstapp.R.string.push_default_message) }

        pushNotificationHelper.showDataPush(kind, title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
