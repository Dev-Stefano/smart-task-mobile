package com.example.taskmanager.screens

// -------------------------------
// Navigation routes for the app
// -------------------------------
// Defines the names of screens used in Navigation Compose.
sealed class NavRoutes(val route: String) {
    object TaskList : NavRoutes("task_list")
    object TaskDetail : NavRoutes("task_detail")
}