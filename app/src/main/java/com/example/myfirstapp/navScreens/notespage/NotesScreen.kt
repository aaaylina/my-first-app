package com.example.myfirstapp.navScreens.notespage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R
import com.example.myfirstapp.model.AppTheme
import com.example.myfirstapp.model.Note
import com.example.myfirstapp.model.AppState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp

@Composable
fun NotesScreen(
    appState: AppState,
    onAddNoteClick: () -> Unit,
    onChangeTheme: (AppTheme) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = getThemeDisplayName(context, appState.selectedTheme),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.select_theme)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                AppTheme.values().forEach { theme ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = getThemeDisplayName(context, theme),
                                color = theme.primaryColor
                            )
                        },
                        onClick = {
                            onChangeTheme(theme)
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.welcome_message, appState.userEmail),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (appState.notes.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_notes),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 16.dp)
            ) {
                items(appState.notes) { note ->
                    NoteItem(note = note)
                }
            }
        }

        Button(
            onClick = onAddNoteClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_note_button))
        }
    }
}

private fun getThemeDisplayName(context: android.content.Context, theme: AppTheme): String {
    return when (theme) {
        AppTheme.PINK -> context.getString(R.string.theme_pink)
        AppTheme.PURPLE -> context.getString(R.string.theme_purple)
        AppTheme.BLUE -> context.getString(R.string.theme_blue)
    }
}

@Composable
fun NoteItem(note: Note) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}