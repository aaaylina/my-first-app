package com.example.myfirstapp.components.coroutines

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.myfirstapp.R
import com.example.myfirstapp.model.DispatcherType

@Composable
fun DispatcherDropdown(
    selectedDispatcher: DispatcherType,
    onDispatcherSelected: (DispatcherType) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val dispatchers = DispatcherType.entries

    Column {
        Text(
            text = stringResource(R.string.dispatcher_label),
            style = MaterialTheme.typography.bodyMedium
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = getDispatcherDisplayName(selectedDispatcher),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                dispatchers.forEach { dispatcher ->
                    DropdownMenuItem(
                        text = { Text(getDispatcherDisplayName(dispatcher)) },
                        onClick = {
                            onDispatcherSelected(dispatcher)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun getDispatcherDisplayName(dispatcher: DispatcherType): String {
    return when (dispatcher) {
        DispatcherType.DEFAULT -> stringResource(R.string.dispatcher_default)
        DispatcherType.IO -> stringResource(R.string.dispatcher_io)
        DispatcherType.MAIN -> stringResource(R.string.dispatcher_main)
    }
}