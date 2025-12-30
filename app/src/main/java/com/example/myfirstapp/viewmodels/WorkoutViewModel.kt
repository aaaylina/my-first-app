package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.App
import com.example.myfirstapp.data.entities.Workout
import com.example.myfirstapp.data.repository.FitnessRepository
import com.example.myfirstapp.data.repository.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.myfirstapp.R

class WorkoutViewModel(private val repository: FitnessRepository) : ViewModel() {

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    private val _uiState = MutableStateFlow<WorkoutUiState>(WorkoutUiState.Idle)
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    private var currentUserId: Long = -1

    fun resetState() {
        _uiState.value = WorkoutUiState.Idle
    }

    fun setUserId(userId: Long) {
        currentUserId = userId
        loadWorkouts()
    }

    fun loadWorkouts() {
        if (currentUserId == -1L) return

        viewModelScope.launch {
            _uiState.value = WorkoutUiState.Loading
            try {
                repository.getWorkouts(currentUserId, _sortType.value)
                    .collectLatest { workoutsList ->
                        _workouts.value = workoutsList
                        _uiState.value = WorkoutUiState.Success
                    }
            } catch (e: Exception) {
                _uiState.value = WorkoutUiState.Error(e.message ?: App.instance.getString(R.string.failed_to_load_w))
            }
        }
    }

    fun addWorkout(workout: Workout) {
        viewModelScope.launch {
            _uiState.value = WorkoutUiState.Loading
            val result = repository.addWorkout(workout)

            _uiState.value = when {
                result.isSuccess -> {
                    loadWorkouts()
                    WorkoutUiState.Success
                }
                else -> WorkoutUiState.Error(App.instance.getString(R.string.failed_to_add))
            }
        }
    }

    fun changeSortType(newSortType: SortType) {
        _sortType.value = newSortType
        loadWorkouts()
    }

    fun rateWorkout(workoutId: Long, rating: Float) {
        viewModelScope.launch {
            repository.rateWorkout(workoutId, rating)
            loadWorkouts()
        }
    }
}

sealed class WorkoutUiState {
    object Idle : WorkoutUiState()
    object Loading : WorkoutUiState()
    object Success : WorkoutUiState()
    data class Error(val message: String) : WorkoutUiState()
}