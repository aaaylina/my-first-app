package com.example.myfirstapp.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.data.entities.Workout
import com.example.myfirstapp.viewmodels.AuthViewModel
import com.example.myfirstapp.viewmodels.WorkoutViewModel
import com.example.myfirstapp.viewmodels.WorkoutUiState
import com.google.gson.Gson
import com.example.myfirstapp.R
import kotlinx.coroutines.launch
import com.example.myfirstapp.App

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    workoutViewModel: WorkoutViewModel,
    authViewModel: AuthViewModel,
    onWorkoutAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Strength") }
    var duration by remember { mutableStateOf("30") }
    var difficulty by remember { mutableStateOf("Beginner") }
    var calories by remember { mutableStateOf("300") }
    var equipment by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }

    val uiState by workoutViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val currentUser by authViewModel.currentUser.collectAsState()
    val userId = currentUser?.id ?: 0L

    val categories = listOf("Strength", "Cardio", "Yoga", "HIIT", "Pilates", "CrossFit")
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    LaunchedEffect(Unit) {
        workoutViewModel.resetState()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is WorkoutUiState.Loading -> isLoading = true
            is WorkoutUiState.Success -> {
                isLoading = false
                onWorkoutAdded()
            }
            is WorkoutUiState.Error -> {
                isLoading = false
                errorMessage = (uiState as WorkoutUiState.Error).message
            }
            else -> isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_new_workout)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            errorMessage?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.workout_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.category)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryExpanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { if (it.all { char -> char.isDigit() }) duration = it },
                    label = { Text(stringResource(R.string.duration)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = calories,
                    onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                    label = { Text(stringResource(R.string.calor)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            ExposedDropdownMenuBox(
                expanded = difficultyExpanded,
                onExpandedChange = { difficultyExpanded = !difficultyExpanded }
            ) {
                OutlinedTextField(
                    value = difficulty,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.difficulty)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = difficultyExpanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = difficultyExpanded,
                    onDismissRequest = { difficultyExpanded = false}
                ) {
                    difficulties.forEach { diff ->
                        DropdownMenuItem(
                            text = { Text(diff) },
                            onClick = {
                                difficulty = diff
                                difficultyExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = equipment,
                onValueChange = { equipment = it },
                label = { Text(stringResource(R.string.equipment)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.equipment_exmp)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() &&
                        duration.isNotBlank() && calories.isNotBlank()) {

                        val equipmentList = equipment.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }

                        val workout = Workout(
                            userId = userId,
                            title = title,
                            description = description,
                            category = category,
                            duration = duration.toIntOrNull() ?: 30,
                            difficulty = difficulty,
                            calories = calories.toIntOrNull() ?: 300,
                            equipment = Gson().toJson(equipmentList),
                            imageUrl = null
                        )

                        scope.launch {
                            workoutViewModel.addWorkout(workout)
                        }
                    } else {
                        errorMessage = App.instance.getString(R.string.fill_all)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.add_workout))
                }
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}