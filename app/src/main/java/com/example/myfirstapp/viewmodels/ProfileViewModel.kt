package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.data.entities.User
import com.example.myfirstapp.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myfirstapp.App
import com.example.myfirstapp.R

class ProfileViewModel(private val repository: FitnessRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _workoutCount = MutableStateFlow(0)
    val workoutCount: StateFlow<Int> = _workoutCount.asStateFlow()

    private var currentUserId: Long = -1

    fun setUserId(userId: Long) {
        currentUserId = userId
        loadUserProfile()
        loadWorkoutCount()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val user = repository.getUserById(currentUserId)
                _userProfile.value = user
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: App.instance.getString(R.string.failed_to_load))
            }
        }
    }

    private fun loadWorkoutCount() {
        viewModelScope.launch {
            val count = repository.getWorkoutCount(currentUserId)
            _workoutCount.value = count
        }
    }

    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                repository.updateUserProfile(updatedUser)
                _userProfile.value = updatedUser
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(App.instance.getString(R.string.failed_to_update))
            }
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}