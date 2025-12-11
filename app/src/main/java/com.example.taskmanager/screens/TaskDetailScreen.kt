package com.example.taskmanager.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.Task
import java.util.*

// -------------------------------
// Screen for adding/editing a task
// -------------------------------
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

    val context = LocalContext.current

    Column(Modifier.padding(16.dp)) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val calendar = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d -> dueDate = "$d/${m+1}/$y" },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) { Text("Select Due Date") }
        Spacer(Modifier.height(8.dp))
        Text("Due Date: $dueDate", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    onSave(Task(task?.id ?: 0, title, description, dueDate))
                }
            }) { Text("Save") }
            Spacer(Modifier.width(8.dp))
            if (task != null) {
                Button(onClick = { onDelete(task) }) { Text("Delete") }
                Spacer(Modifier.width(8.dp))
            }
            OutlinedButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}