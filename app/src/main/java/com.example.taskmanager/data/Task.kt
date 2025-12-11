package com.example.taskmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// -------------------------------
// Task entity for Room database
// -------------------------------
// @Entity tells Room this class represents a table in the database.
// Each property becomes a column in the table.
@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique ID, auto-generated
    val title: String,                                // Task title
    val description: String,                          // Task description
    val dueDate: String                               // Task due date (stored as String for simplicity)
)