package com.example.myfirstapp.navScreens.addnotepage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R

@Composable
fun AddNoteScreen(
    onSaveNote: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.new_note_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            label = { Text(stringResource(R.string.note_title_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            isError = titleError,
            singleLine = true
        )
        if (titleError) {
            Text(
                text = stringResource(R.string.empty_title),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(stringResource(R.string.note_content_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = true
                } else {
                    onSaveNote(title, content)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_button))
        }
    }
}