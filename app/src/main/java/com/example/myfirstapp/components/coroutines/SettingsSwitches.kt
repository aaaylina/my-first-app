package com.example.myfirstapp.components.coroutines

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R

@Composable
fun SettingsSwitches(
    sequential: Boolean,
    onSequentialChange: (Boolean) -> Unit,
    lazyLaunch: Boolean,
    onLazyLaunchChange: (Boolean) -> Unit,
    backgroundWork: Boolean,
    onBackgroundWorkChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SwitchRow(
            text = stringResource(R.string.sequential_switch),
            checked = sequential,
            onCheckedChange = {
                if (it) {
                    onSequentialChange(true)
                } else {
                    onSequentialChange(false)
                }
            }
        )

        SwitchRow(
            text = stringResource(R.string.parallel_switch),
            checked = !sequential,
            onCheckedChange = {
                if (it) {
                    onSequentialChange(false)
                } else {
                    onSequentialChange(true)
                }
            }
        )

        SwitchRow(
            text = stringResource(R.string.lazy_launch_switch),
            checked = lazyLaunch,
            onCheckedChange = onLazyLaunchChange
        )

        SwitchRow(
            text = stringResource(R.string.background_work_switch),
            checked = backgroundWork,
            onCheckedChange = onBackgroundWorkChange
        )
    }
}

@Composable
private fun SwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}