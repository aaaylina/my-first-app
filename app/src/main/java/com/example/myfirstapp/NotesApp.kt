package com.example.myfirstapp

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.model.AppState
import com.example.myfirstapp.ui.theme.NotesAppTheme
import com.example.myfirstapp.navScreens.loginpage.LoginScreen
import com.example.myfirstapp.navScreens.notespage.NotesScreen
import com.example.myfirstapp.navScreens.addnotepage.AddNoteScreen


@Composable
fun NotesApp() {
    val navController = rememberNavController()

    var appState by remember { mutableStateOf(AppState()) }


    NotesAppTheme(appTheme = appState.selectedTheme) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { email ->
                        appState = appState.login(email)
                        navController.navigate("notes")
                    }
                )
            }

            composable("notes") {
                NotesScreen(
                    appState = appState,
                    onAddNoteClick = {
                        navController.navigate("add_note")
                    },
                    onChangeTheme = { theme ->
                        appState = appState.changeTheme(theme)
                    }
                )
            }

            composable("add_note") {
                AddNoteScreen(
                    onSaveNote = { title, content ->
                        appState = appState.addNote(title, content)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}