package com.example.taskmanager


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanager.data.TaskDatabase
import com.example.taskmanager.data.TaskRepository
import com.example.taskmanager.screens.NavRoutes
import com.example.taskmanager.screens.TaskDetailScreen
import com.example.taskmanager.screens.TaskListScreen
import com.example.taskmanager.viewmodel.TaskViewModel
import com.example.taskmanager.viewmodel.TaskViewModelFactory


// -------------------------------
// Main entry point of the app
// -------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create database + repository
        val database = TaskDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TaskManagerApp(repository)
                }
            }
        }
    }
}

@Composable
fun TaskManagerApp(repository: TaskRepository) {
    val navController = rememberNavController()

    // ✅ Use the factory we defined inside TaskViewModel.kt
    val viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(repository)
    )

    NavHost(navController = navController, startDestination = NavRoutes.TaskList.route) {
        composable(NavRoutes.TaskList.route) {
            TaskListScreen(
                viewModel = viewModel,
                onAddTask = { navController.navigate(NavRoutes.TaskDetail.route) },
                onEditTask = { task ->
                    navController.navigate("${NavRoutes.TaskDetail.route}/${task.id}")
                }
            )
        }
        composable(NavRoutes.TaskDetail.route) {
            TaskDetailScreen(
                onSave = { viewModel.insert(it); navController.popBackStack() },
                onDelete = { viewModel.delete(it); navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = "${NavRoutes.TaskDetail.route}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            val task = viewModel.allTasks.value?.find { it.id == taskId }
            TaskDetailScreen(
                task = task,
                onSave = { viewModel.update(it); navController.popBackStack() },
                onDelete = { viewModel.delete(it); navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
