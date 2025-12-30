package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.App
import com.example.myfirstapp.data.entities.User
import com.example.myfirstapp.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myfirstapp.R

class AuthViewModel(private val repository: FitnessRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            repository.cleanupDeletedUsers()
        }
    }

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.registerUser(email, password, username)
            _uiState.value = when {
                result.isSuccess -> AuthUiState.Success(result.getOrNull() ?: 0L)
                else -> AuthUiState.Error(result.exceptionOrNull()?.message ?: App.instance.getString(R.string.registration_failed))
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.loginUser(email, password)
            when {
                result.isSuccess -> {
                    val user = result.getOrNull()
                    if (user?.deletedAt != null) {
                        _uiState.value = AuthUiState.AccountDeleted(user)
                    } else {
                        _currentUser.value = user
                        _isLoggedIn.value = true
                        _uiState.value = AuthUiState.Success(user?.id ?: 0L)
                    }
                }
                else -> {
                    _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: App.instance.getString(R.string.login_failed))
                }
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _uiState.value = AuthUiState.Idle
    }

    fun restoreAccount(userId: Long) {
        viewModelScope.launch {
            repository.restoreUser(userId)
            val user = repository.getUserById(userId)
            _currentUser.value = user
            _isLoggedIn.value = true
            _uiState.value = AuthUiState.Success(userId)
        }
    }

    fun permanentlyDeleteAccount(userId: Long) {
        viewModelScope.launch {
            repository.permanentlyDeleteUser(userId)
            logout()
        }
    }

    fun softDeleteAccount(userId: Long) {
        viewModelScope.launch {
            repository.softDeleteUser(userId)
            logout()
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val userId: Long) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data class AccountDeleted(val user: User) : AuthUiState()
}
