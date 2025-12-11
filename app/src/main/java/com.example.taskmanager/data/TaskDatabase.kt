package com.example.taskmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// -------------------------------
// Room Database for the Task entity
// -------------------------------
// @Database annotation tells Room which entities are included
// and sets the database version.
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    // Expose the DAO to the rest of the app
    abstract fun taskDao(): TaskDao

    companion object {
        // Singleton instance to prevent multiple database objects
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        // Get the database instance (create if not exists)
        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database" // Name of the database file
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}