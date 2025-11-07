package com.example.myfirstapp.navScreens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.myfirstapp.R
import com.example.myfirstapp.utils.NotificationHandler

@Composable
fun EditNotificationScreen() {
    val context = LocalContext.current
    val notificationHandler= remember { NotificationHandler(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var notificationId by remember { mutableStateOf("") }
    var newText by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.edit_notification),
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = notificationId,
                onValueChange = { notificationId = it },
                label = { Text(stringResource(R.string.notification_id)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                label = { Text(stringResource(R.string.new_text)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val id = notificationId.toIntOrNull()
                    if (id != null && newText.isNotEmpty()) {
                        val success = notificationHandler.updateNotification(id, newText)
                        scope.launch {
                            if (success) {
                                snackbarHostState.showSnackbar(context.getString(R.string.notification_updated))
                            } else {
                                snackbarHostState.showSnackbar(context.getString(R.string.notification_not_found))
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.enter_valid_id))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_notification))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (notificationHandler.hasActiveNotifications()) {
                        notificationHandler.cancelAllNotifications()
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.all_notifications_cleared))
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.no_active_notifications))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.clear_all_notifications))
            }
        }
    }
}