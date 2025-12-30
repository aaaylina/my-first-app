package com.example.myfirstapp.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.App
import com.example.myfirstapp.data.entities.Workout
import com.example.myfirstapp.data.repository.SortType
import com.example.myfirstapp.viewmodels.AuthViewModel
import com.example.myfirstapp.viewmodels.WorkoutViewModel
import com.example.myfirstapp.viewmodels.WorkoutUiState
import kotlinx.coroutines.launch
import com.example.myfirstapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsListScreen(
    workoutViewModel: WorkoutViewModel,
    authViewModel: AuthViewModel,
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val workouts by workoutViewModel.workouts.collectAsState()
    val uiState by workoutViewModel.uiState.collectAsState()
    val sortType by workoutViewModel.sortType.collectAsState()

    var showSortBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.my_workouts)) },
                actions = {
                    IconButton(onClick = { showSortBottomSheet = true }) {
                        Icon(Icons.Default.Sort, contentDescription = App.instance.getString(R.string.sort))
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = App.instance.getString(R.string.profile_tab))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigateToAddWorkout()
            }) {
                Icon(Icons.Default.Add, contentDescription = App.instance.getString(R.string.add_workout))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                is WorkoutUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.loading_workouts))
                    }
                }
                is WorkoutUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (uiState as WorkoutUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { workoutViewModel.loadWorkouts() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                else -> {
                    if (workouts.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_workouts_yet),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.tap_plus),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(workouts) { workout ->
                                WorkoutCard(workout = workout)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSortBottomSheet) {
        SortBottomSheet(
            currentSortType = sortType,
            onSortTypeSelected = { newSortType ->
                scope.launch {
                    workoutViewModel.changeSortType(newSortType)
                }
                showSortBottomSheet = false
            },
            onDismiss = { showSortBottomSheet = false }
        )
    }
}

@Composable
fun WorkoutCard(workout: Workout) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = workout.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.rting),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", workout.rating),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = workout.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text(workout.category) },
                    modifier = Modifier
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${workout.duration} min",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.calories} cal",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Difficulty: ${workout.difficulty}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            SortType.values().forEach { sortType ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSortType == sortType,
                        onClick = { onSortTypeSelected(sortType) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when (sortType) {
                            SortType.DATE -> stringResource(R.string.date)
                            SortType.TITLE -> stringResource(R.string.title)
                            SortType.DURATION -> stringResource(R.string.duration_sort)
                            SortType.DIFFICULTY -> stringResource(R.string.difficulty_sort)
                            SortType.RATING -> stringResource(R.string.rating)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.close))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}