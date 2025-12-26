package com.example.myfirstapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.data.database.AppDatabase
import com.example.myfirstapp.data.repository.FitnessRepository
import com.example.myfirstapp.navigation.NavigationRoutes
import com.example.myfirstapp.ui.screens.auth.LoginScreen
import com.example.myfirstapp.ui.screens.auth.RecoverAccountScreen
import com.example.myfirstapp.ui.screens.auth.RegisterScreen
import com.example.myfirstapp.ui.screens.main.AddWorkoutScreen
import com.example.myfirstapp.ui.screens.main.ProfileScreen
import com.example.myfirstapp.ui.screens.main.WorkoutsListScreen
import com.example.myfirstapp.ui.theme.FitnessAppTheme
import com.example.myfirstapp.utils.SessionManager
import com.example.myfirstapp.viewmodels.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FitnessRepository(
            database.userDao(),
            database.workoutDao()
        )
        val viewModelFactory = ViewModelFactory(repository)

        setContent {
            FitnessApp(
                viewModelFactory = viewModelFactory,
                sessionManager = sessionManager,
                repository = repository
                )
        }
    }
}

@Composable
fun FitnessApp(
    viewModelFactory: ViewModelFactory,
    sessionManager: SessionManager? = null,
    repository: FitnessRepository? = null
) {
    FitnessAppTheme {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
        val workoutViewModel: WorkoutViewModel = viewModel(factory = viewModelFactory)
        val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)

        val authUiState by authViewModel.uiState.collectAsState()
        val currentUser by authViewModel.currentUser.collectAsState()
        val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

        var isNavigating by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(navController) {
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
            }
        }

        LaunchedEffect(Unit) {
            sessionManager?.let { manager ->
                if (manager.isLoggedIn()) {
                    val userId = manager.getUserId()
                    val userEmail = manager.getUserEmail()

                    if (userId != -1L && userEmail.isNotBlank()) {
                        repository?.let { repo ->
                            val user = repo.getUserById(userId)

                            user?.let {
                                if (it.deletedAt == null) {
                                    authViewModel.restoreAccount(userId)
                                    workoutViewModel.setUserId(userId)
                                    profileViewModel.setUserId(userId)

                                    navController.navigate(NavigationRoutes.WORKOUTS_LIST) {
                                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                                    }
                                } else {
                                    val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
                                    if (it.deletedAt > sevenDaysAgo) {
                                        navController.navigate(NavigationRoutes.RECOVER_ACCOUNT) {
                                            popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                                        }
                                    } else {
                                        repo.permanentlyDeleteUser(userId)
                                        manager.clearSession()
                                    }
                                }
                            } ?: run {
                                manager.clearSession()
                            }
                        }
                    } else {
                        manager.clearSession()
                    }
                } else {
                }
            }

        }

        LaunchedEffect(currentUser) {
            currentUser?.let { user ->
                sessionManager?.saveLoginSession(user.id, user.email)
            }
        }

        LaunchedEffect(isLoggedIn) {
            if (!isLoggedIn) {
                sessionManager?.clearSession()
            }
        }

        LaunchedEffect(authUiState) {
            when (authUiState) {
                is AuthUiState.AccountDeleted -> {
                    navController.navigate(NavigationRoutes.RECOVER_ACCOUNT) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                }

                else -> {}
            }
        }

        Scaffold(

        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = NavigationRoutes.LOGIN,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(NavigationRoutes.LOGIN) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { userId ->
                            workoutViewModel.setUserId(userId)
                            profileViewModel.setUserId(userId)
                            navController.navigate(NavigationRoutes.WORKOUTS_LIST) {
                                popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate(NavigationRoutes.REGISTER)
                        }
                    )
                }

                composable(NavigationRoutes.REGISTER) {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterSuccess = { userId ->
                            workoutViewModel.setUserId(userId)
                            profileViewModel.setUserId(userId)
                            navController.navigate(NavigationRoutes.WORKOUTS_LIST) {
                                popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(NavigationRoutes.RECOVER_ACCOUNT) {

                    val user = when (authUiState) {
                        is AuthUiState.AccountDeleted -> (authUiState as AuthUiState.AccountDeleted).user
                        else -> null
                    }

                    RecoverAccountScreen(
                        user = user,
                        onRestoreAccount = { userId ->
                            authViewModel.restoreAccount(userId)
                            workoutViewModel.setUserId(userId)
                            profileViewModel.setUserId(userId)
                            navController.navigate(NavigationRoutes.WORKOUTS_LIST) {
                                popUpTo(NavigationRoutes.RECOVER_ACCOUNT) { inclusive = true }
                            }
                        },
                        onPermanentlyDelete = { userId ->
                            authViewModel.permanentlyDeleteAccount(userId)
                            navController.navigate(NavigationRoutes.LOGIN) {
                                popUpTo(NavigationRoutes.RECOVER_ACCOUNT) { inclusive = true }
                            }
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(NavigationRoutes.WORKOUTS_LIST) {

                    WorkoutsListScreen(
                        workoutViewModel = workoutViewModel,
                        authViewModel = authViewModel,
                        onNavigateToAddWorkout = {
                            navController.navigate(NavigationRoutes.ADD_WORKOUT)
                        },
                        onNavigateToProfile = {
                            navController.navigate(NavigationRoutes.PROFILE)
                        }
                    )
                }

                composable(NavigationRoutes.ADD_WORKOUT) {
                    LaunchedEffect(Unit) {
                        isNavigating = false

                        workoutViewModel.resetState()
                    }

                    AddWorkoutScreen(
                        workoutViewModel = workoutViewModel,
                        authViewModel = authViewModel,
                        onWorkoutAdded = {
                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(NavigationRoutes.PROFILE) {
                    ProfileScreen(
                        profileViewModel = profileViewModel,
                        authViewModel = authViewModel,
                        onLogout = {
                            authViewModel.logout()
                            sessionManager?.clearSession()
                            navController.navigate(NavigationRoutes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onDeleteAccount = { userId ->
                            authViewModel.softDeleteAccount(userId)
                            sessionManager?.clearSession()
                            navController.navigate(NavigationRoutes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}