package com.example.assignment_3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Defines a Task entity for the Room database, representing a single task in the task management app.
// The @Entity annotation tells Room that this class represents a table in the database.
@Entity(tableName = "task")
data class Task(
    // The @PrimaryKey annotation marks the 'id' field as the primary key for the table.
    // autoGenerate = true means Room will automatically generate a unique ID for each task.
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // The name of the task (e.g., "Finish Assignment").
    // This field is required and stored as a TEXT column in the database.
    val name: String,

    // A brief description of the task (e.g., "Complete the Android app project").
    // This field is stored as a TEXT column in the database.
    val description: String,

    // The due date and time of the task in the format "dd/MM/yyyy HH:mm" (e.g., "15/04/2025 14:30").
    // This field is stored as a TEXT column in the database.
    val dueDate: String,

    // The priority level of the task, which can be "High", "Medium", or "Low".
    // This field is stored as a TEXT column in the database.
    val priority: String, // "High", "Medium", "Low"

    // Indicates whether the task is completed (true) or not (false).
    // This field is stored as an INTEGER column in the database (0 for false, 1 for true).
    val isCompleted: Boolean
)