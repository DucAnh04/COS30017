package com.example.assignment_3.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query

// Marks this interface as a Data Access Object (DAO) for Room database operations
@Dao
interface TaskDao {
    // Inserts a new task into the database
    // suspend keyword allows this function to be called from a coroutine
    @Insert
    suspend fun insert(task: Task)

    // Updates an existing task in the database
    // suspend keyword enables coroutine support
    @Update
    suspend fun update(task: Task)

    // Deletes a task from the database
    // suspend keyword allows this to run asynchronously
    @Delete
    suspend fun delete(task: Task)

    // Retrieves all tasks from the database
    // Returns LiveData to automatically notify observers of data changes
    @Query("SELECT * FROM task")
    fun getAllTasks(): LiveData<List<Task>>

    // Gets tasks filtered by priority level
    // Takes a priority string as parameter and returns matching tasks
    @Query("SELECT * FROM task WHERE priority = :priority")
    fun getTasksByPriority(priority: String): LiveData<List<Task>>

    // Returns the total count of tasks in the database
    // Useful for statistics or displaying total task number
    @Query("SELECT COUNT(*) FROM task")
    fun getTotalTasks(): LiveData<Int>

    // Returns the count of completed tasks
    // isCompleted = 1 checks for tasks marked as done
    @Query("SELECT COUNT(*) FROM task WHERE isCompleted = 1")
    fun getCompletedTasks(): LiveData<Int>

    // New queries
    // Counts tasks completed today
    // Uses LIKE with date pattern matching for today's date
    @Query("SELECT COUNT(*) FROM task WHERE isCompleted = 1 AND dueDate LIKE :todayDate || '%'")
    fun getCompletedTasksToday(todayDate: String): LiveData<Int>

    // Counts tasks due within a specific week
    // Uses BETWEEN to check dates within start and end of week parameters
    @Query("SELECT COUNT(*) FROM task WHERE dueDate BETWEEN :startOfWeek AND :endOfWeek")
    fun getTasksDueThisWeek(startOfWeek: String, endOfWeek: String): LiveData<Int>
}