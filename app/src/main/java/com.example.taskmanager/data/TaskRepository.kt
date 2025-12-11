package com.example.taskmanager.data

import androidx.lifecycle.LiveData

// -------------------------------
// Repository layer
// -------------------------------
// Acts as a clean API for the rest of the app.
// It abstracts away whether data comes from Room, network, etc.
class TaskRepository(private val taskDao: TaskDao) {

    // Expose all tasks as LiveData
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // Insert a new task
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    // Update an existing task
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    // Delete a task
    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
}