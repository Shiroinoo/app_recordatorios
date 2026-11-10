package com.example.app_recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_recordatorios.screens.CreateTaskScreen
import com.example.app_recordatorios.screens.HomeScreen
import com.example.app_recordatorios.ui.theme.AppTheme
import com.example.app_recordatorios.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkMode by rememberSaveable { mutableStateOf(systemDark) }
            var creating by remember { mutableStateOf(false) }
            val viewModel: EventViewModel = viewModel()

            AppTheme(darkTheme = darkMode) {
                if (creating) {
                    CreateTaskScreen(
                        onClose = { creating = false },
                        onSaved = { creating = false },
                        viewModel = viewModel,
                        isDark = darkMode,
                        onToggleTheme = { darkMode = !darkMode }
                    )
                } else {
                    HomeScreen(
                        onCreateClick = { creating = true },
                        viewModel = viewModel,
                        isDark = darkMode,
                        onToggleTheme = { darkMode = !darkMode }
                    )
                }
            }
        }
    }
}