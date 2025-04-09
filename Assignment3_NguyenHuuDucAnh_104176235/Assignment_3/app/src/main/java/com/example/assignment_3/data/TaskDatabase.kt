package com.example.assignment_3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Defines the Room database with its entities and version
// entities specifies the tables (Task class in this case)
// version tracks database schema version
// exportSchema = false prevents schema export to avoid version control clutter
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    // Abstract method to provide access to the TaskDao
    // Room will generate the implementation
    abstract fun taskDao(): TaskDao

    // Companion object for singleton pattern implementation
    companion object {
        // Volatile annotation ensures thread-safe visibility of INSTANCE
        // Prevents multiple threads from creating separate instances
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        // Provides a thread-safe way to get the database instance
        // Creates the database if it doesn't exist
        fun getDatabase(context: Context): TaskDatabase {
            // Returns existing instance if available, otherwise creates new one
            return INSTANCE ?: synchronized(this) {
                // synchronized block ensures only one thread creates the instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,  // Uses application context to avoid memory leaks
                    TaskDatabase::class.java,    // Specifies the database class
                    "task_database"             // Name of the database file
                ).build()                       // Builds the database instance
                INSTANCE = instance          // Stores the instance for future use
                instance                     // Returns the created instance
            }
        }
    }
}