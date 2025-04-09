package com.example.assignment_3.data

import androidx.lifecycle.LiveData
import java.text.SimpleDateFormat
import java.util.*

// TaskRepository serves as an intermediary between the ViewModel and the TaskDao,
// providing a clean API for data operations and abstracting database interactions.
class TaskRepository(private val taskDao: TaskDao) {

    // Holds a LiveData list of all tasks, automatically updated when the database changes.
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // Inserts a new task into the database. Marked as suspend to run in a coroutine scope.
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    // Updates an existing task in the database. Marked as suspend for coroutine execution.
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    // Deletes a task from the database. Marked as suspend to ensure safe coroutine execution.
    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    // Retrieves tasks filtered by priority, returning a LiveData list for UI observation.
    fun getTasksByPriority(priority: String): LiveData<List<Task>> {
        return taskDao.getTasksByPriority(priority)
    }

    // Returns a LiveData object containing the total number of tasks in the database.
    fun getTotalTasks(): LiveData<Int> {
        return taskDao.getTotalTasks()
    }

    // Returns a LiveData object with the count of completed tasks in the database.
    fun getCompletedTasks(): LiveData<Int> {
        return taskDao.getCompletedTasks()
    }

    // Retrieves the number of tasks completed today, based on the current date.
    // Uses SimpleDateFormat to format today's date for the database query.
    fun getCompletedTasksToday(): LiveData<Int> {
        // Format the current date as "dd/MM/yyyy" for consistency with database records.
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        // Query the database for tasks completed on the formatted date.
        return taskDao.getCompletedTasksToday(today)
    }

    // Retrieves the number of tasks due within the current week (Monday to Sunday).
    // Uses Calendar to calculate the start and end dates of the week.
    fun getTasksDueThisWeek(): LiveData<Int> {
        // Initialize a Calendar instance to manipulate dates.
        val calendar = Calendar.getInstance()
        // Set the calendar to the first day of the current week (Monday).
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        // Format the start of the week as "dd/MM/yyyy".
        val startOfWeek = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        // Add 6 days to get the end of the week (Sunday).
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        // Format the end of the week as "dd/MM/yyyy".
        val endOfWeek = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        // Query the database for tasks due between startOfWeek and endOfWeek.
        return taskDao.getTasksDueThisWeek(startOfWeek, endOfWeek)
    }
}