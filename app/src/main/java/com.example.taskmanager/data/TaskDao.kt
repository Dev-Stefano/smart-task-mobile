package com.example.taskmanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

// -------------------------------
// Data Access Object (DAO) for Room
// -------------------------------
// This interface defines all the database operations
// we can perform on the Task table.
@Dao
interface TaskDao {

    // Insert a new task into the database
    @Insert
    suspend fun insert(task: Task)

    // Update an existing task
    @Update
    suspend fun update(task: Task)

    // Delete a task
    @Delete
    suspend fun delete(task: Task)

    // Query all tasks, ordered by ID
    // LiveData ensures the UI updates automatically when data changes
    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<Task>>
}