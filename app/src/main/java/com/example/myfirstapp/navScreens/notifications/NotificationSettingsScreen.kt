package com.example.myfirstapp.navScreens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R
import com.example.myfirstapp.model.NotificationData
import com.example.myfirstapp.model.NotificationPriority
import androidx.compose.ui.text.font.FontWeight
@Composable
fun NotificationSettingsScreen(
    onSendNotification: (NotificationData) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isExpandable by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf(NotificationPriority.MEDIUM) }
    var shouldOpenApp by remember { mutableStateOf(false) }
    var hasReplyAction by remember { mutableStateOf(false) }

    var lastNotificationId by remember { mutableStateOf<Int?>(null) }

    val isTitleError by remember { derivedStateOf { title.isEmpty() } }
    val isSendEnabled by remember { derivedStateOf { title.isNotEmpty() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.notification_settings_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        lastNotificationId?.let { id ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.id_notification),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$id",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.notification_title)) },
            isError = isTitleError,
            supportingText = {
                if (isTitleError) {
                    Text(stringResource(R.string.notification_title_error))
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text(stringResource(R.string.notification_message)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.expandable_switch_label))
            Switch(
                checked = isExpandable,
                onCheckedChange = { isExpandable = it },
                enabled = message.isNotEmpty()
            )
        }

        Text(stringResource(R.string.priority_label))
        NotificationPriority.values().forEach { currentPriority ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = priority == currentPriority,
                    onClick = { priority = currentPriority }
                )
                Text(
                    text = when (currentPriority) {
                        NotificationPriority.MIN -> stringResource(R.string.priority_min)
                        NotificationPriority.LOW -> stringResource(R.string.priority_low)
                        NotificationPriority.MEDIUM -> stringResource(R.string.priority_medium)
                        NotificationPriority.HIGH -> stringResource(R.string.priority_high)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.open_app_switch))
            Switch(
                checked = shouldOpenApp,
                onCheckedChange = { shouldOpenApp = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.reply_action_switch))
            Switch(
                checked = hasReplyAction,
                onCheckedChange = { hasReplyAction = it }
            )
        }

        Button(
            onClick = {
                val notificationData = NotificationData(
                    title = title,
                    message = message,
                    isExpandable = isExpandable && message.isNotEmpty(),
                    priority = priority,
                    shouldOpenApp = shouldOpenApp,
                    hasReplyAction = hasReplyAction
                )
                lastNotificationId = notificationData.notificationId
                onSendNotification(notificationData)
            },
            enabled = isSendEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.send_notification))
        }
    }
}