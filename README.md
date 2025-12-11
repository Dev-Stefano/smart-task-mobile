📱 Smart Task Mobile
Offline task manager built with Kotlin, Jetpack Compose, Room, and MVVM. Add, edit, delete tasks persistently without a backend.
## Roadmap
- Backend API integration
- Authentication
- AI-powered task suggestions
  
🚀 How to run
Clone the repo:

bash
git clone https://github.com/your-username/smart-task-mobile.git
Open in Android Studio (Giraffe or newer) with Kotlin plugin enabled.

Run on an emulator or device (Android 8.0+).

✨ Features
Offline persistence with Room

Add, edit, delete tasks

Compose UI with Material 3

MVVM architecture (ViewModel + Repository)

Navigation between list and detail screens

Date picker for due dates

📂 Project structure
Code
smart-task-mobile/
 ├── data/
 │    ├── Task.kt
 │    ├── TaskDao.kt
 │    ├── TaskDatabase.kt
 │    └── TaskRepository.kt
 ├── viewmodel/
 │    ├── TaskViewModel.kt
 │    └── TaskViewModelFactory.kt
 ├── screens/
 │    ├── TaskListScreen.kt
 │    ├── TaskDetailScreen.kt
 │    └── NavRoutes.kt
 ├── MainActivity.kt
 └── README.md
🧱 Data layer (Room)
kotlin
// Task.kt
@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: String
)
kotlin
// TaskDao.kt
@Dao
interface TaskDao {
    @Insert suspend fun insert(task: Task)
    @Update suspend fun update(task: Task)
    @Delete suspend fun delete(task: Task)

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<Task>>

    // Improvement: direct lookup by ID
    @Query("SELECT * FROM task_table WHERE id = :id LIMIT 1")
    fun getTaskById(id: Int): LiveData<Task?>
}
kotlin
// TaskDatabase.kt
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object { /* singleton builder */ }
}
kotlin
// TaskRepository.kt
class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    suspend fun insert(task: Task) = taskDao.insert(task)
    suspend fun update(task: Task) = taskDao.update(task)
    suspend fun delete(task: Task) = taskDao.delete(task)

    // Improvement: expose single-task lookup
    fun getTaskById(id: Int): LiveData<Task?> = taskDao.getTaskById(id)
}
🧠 ViewModel layer (MVVM)
kotlin
// TaskViewModel.kt
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    val allTasks: LiveData<List<Task>> = repository.allTasks

    fun insert(task: Task) = viewModelScope.launch { repository.insert(task) }
    fun update(task: Task) = viewModelScope.launch { repository.update(task) }
    fun delete(task: Task) = viewModelScope.launch { repository.delete(task) }

    // Improvement: single-task stream by ID
    fun getTaskById(id: Int): LiveData<Task?> = repository.getTaskById(id)
}
kotlin
// TaskViewModelFactory.kt
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { /* ... */ }
}
🎨 UI layer (Compose)
kotlin
// TaskListScreen.kt
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit
) {
    val tasks by viewModel.allTasks.observeAsState(emptyList())
    Scaffold(
        topBar = { TopAppBar(title = { Text("Tasks") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) { Icon(Icons.Filled.Add, "Add Task") }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(tasks) { task ->
                Card(Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(task.title, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(task.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Row {
                            IconButton({ onEditTask(task) }) { Icon(Icons.Filled.Edit, "Edit Task") }
                            IconButton({ viewModel.delete(task) }) { Icon(Icons.Filled.Delete, "Delete Task") }
                        }
                    }
                }
            }
        }
    }
}
kotlin
// TaskDetailScreen.kt
@Composable
fun TaskDetailScreen(
    task: Task? = null,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: "") }
    val isValid = title.isNotBlank() && description.isNotBlank() && dueDate.isNotBlank()

    val context = LocalContext.current
    Column(Modifier.padding(16.dp)) {
        TextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextField(description, { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val c = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d -> dueDate = "%04d-%02d-%02d".format(y, m + 1, d) },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) { Text("Select Due Date") }
        Spacer(Modifier.height(8.dp))
        Text("Due Date: $dueDate", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = { onSave(Task(task?.id ?: 0, title, description, dueDate)) }, enabled = isValid) { Text("Save") }
            Spacer(Modifier.width(8.dp))
            if (task != null) {
                Button(onClick = { onDelete(task) }) { Text("Delete") }
                Spacer(Modifier.width(8.dp))
            }
            OutlinedButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}
kotlin
// NavRoutes.kt
sealed class NavRoutes(val route: String) {
    object TaskList : NavRoutes("task_list")
    object TaskDetail : NavRoutes("task_detail")
    // Improvement: separate add/edit
    object AddTask : NavRoutes("task_add")
    object EditTask : NavRoutes("task_edit/{taskId}")
}
kotlin
// MainActivity.kt (navigation excerpt)
NavHost(navController, startDestination = NavRoutes.TaskList.route) {
    composable(NavRoutes.TaskList.route) {
        TaskListScreen(
            viewModel = viewModel,
            onAddTask = { navController.navigate(NavRoutes.AddTask.route) },
            onEditTask = { task -> navController.navigate("task_edit/${task.id}") }
        )
    }
    composable(NavRoutes.AddTask.route) {
        TaskDetailScreen(
            onSave = { viewModel.insert(it); navController.navigateUp() },
            onDelete = { /* no-op in add flow */ },
            onCancel = { navController.navigateUp() }
        )
    }
    composable(
        route = NavRoutes.EditTask.route,
        arguments = listOf(navArgument("taskId") { type = NavType.IntType })
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getInt("taskId") ?: -1
        val task by viewModel.getTaskById(id).observeAsState()
        TaskDetailScreen(
            task = task,
            onSave = { viewModel.update(it); navController.navigateUp() },
            onDelete = { task?.let { viewModel.delete(it) }; navController.navigateUp() },
            onCancel = { navController.navigateUp() }
        )
    }
}
📸 Screenshots
Add images to screenshots/ and embed:

markdown
## 📸 Screenshots

### Task list
![Task List](screenshots/task_list.png)

### Add task
![Add Task](screenshots/add_task.png)

### Edit task
![Edit Task](screenshots/edit_task.png)
