package com.example.assignment_3.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.assignment_3.data.Task
import com.example.assignment_3.data.TaskDatabase
import com.example.assignment_3.data.TaskRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TaskViewModel"  // Added TAG constant for logging

// TaskViewModel is a ViewModel that manages task-related data and operations for the UI.
// It extends AndroidViewModel to access the application context and uses a TaskRepository to interact with the database.
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    // Repository instance to handle database operations.
    private val repository: TaskRepository

    // LiveData object that holds the list of all tasks, updated automatically when the database changes.
    val allTasks: LiveData<List<Task>>

    // MutableLiveData to store the current filter (e.g., "All", "High", "Medium", "Low").
    // Used to filter tasks by priority.
    private val _currentFilter = MutableLiveData<String>("All")

    // Public LiveData to expose the current filter to the UI for observation.
    val currentFilter: LiveData<String> get() = _currentFilter

    // LiveData object that holds the filtered list of tasks based on the current filter.
    // Updates automatically when the filter changes.
    val filteredTasks: LiveData<List<Task>>

    // Add LiveData for the count of overdue tasks
    val overdueTasksCount: LiveData<Int>

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Initialization block to set up the ViewModel.
    init {
        // Get the TaskDao instance from the TaskDatabase singleton, using the application context.
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        // Initialize the repository with the TaskDao.
        repository = TaskRepository(taskDao)
        // Assign the repository's allTasks LiveData to the ViewModel's allTasks property.
        allTasks = repository.allTasks
        // Set up filteredTasks to dynamically switch between all tasks or filtered tasks based on the current filter.
        filteredTasks = _currentFilter.switchMap { priority: String ->
            // If the filter is "All", return all tasks; otherwise, fetch tasks by the specified priority.
            if (priority == "All") allTasks else repository.getTasksByPriority(priority)
        }
        // Set up overdueTasksCount by filtering allTasks for overdue tasks
        overdueTasksCount = allTasks.switchMap { tasks ->
            val overdueCount = tasks.count { task ->
                !task.isCompleted && isTaskOverdue(task)
            }
            MutableLiveData(overdueCount)
        }
        Log.d(TAG, "ViewModel initialized, initial filter: ${_currentFilter.value}")
    }

    // Check if a task is overdue by comparing its due date with the current date
    fun isTaskOverdue(task: Task): Boolean {
        return try {
            val dueDate = dateFormatter.parse(task.dueDate)
            val currentDate = Calendar.getInstance().time
            dueDate != null && dueDate.before(currentDate) // Task is overdue if due date is before now
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing due date for task ${task.name}: ${e.message}")
            false // If parsing fails, assume the task is not overdue
        }
    }

    // Sets the current filter to the specified priority, triggering an update to filteredTasks.
    fun setFilter(priority: String) {
        _currentFilter.value = priority // Update the filter value, which will update filteredTasks via switchMap.
        Log.d(TAG, "Filter set to: $priority")
    }

    // Inserts a new task into the database.
    // Uses a coroutine to perform the operation on a background thread.
    fun insert(task: Task) = viewModelScope.launch {
        Log.d(TAG, "Inserting task: ${task.name}, id: ${task.id}")
        repository.insert(task) // Calls the repository's insert method to add the task to the database.
        Log.d(TAG, "Task inserted: ${task.name}")
    }

    // Updates an existing task in the database.
    // Uses a coroutine to perform the operation on a background thread.
    fun update(task: Task) = viewModelScope.launch {
        Log.d(TAG, "Updating task: ${task.name}, id: ${task.id}")
        repository.update(task) // Calls the repository's update method to modify the task in the database.
        Log.d(TAG, "Task updated: ${task.name}")
    }

    // Deletes a task from the database.
    // Uses a coroutine to perform the operation on a background thread.
    fun delete(task: Task) = viewModelScope.launch {
        Log.d(TAG, "Deleting task: ${task.name}, id: ${task.id}")
        repository.delete(task) // Calls the repository's delete method to remove the task from the database.
        Log.d(TAG, "Task deleted: ${task.name}")
    }

    // Retrieves tasks filtered by the specified priority.
    // Returns a LiveData object for the UI to observe.
    fun getTasksByPriority(priority: String): LiveData<List<Task>> {
        Log.d(TAG, "Fetching tasks by priority: $priority")
        return repository.getTasksByPriority(priority) // Delegates to the repository to fetch tasks by priority.
    }

    // Retrieves the total number of tasks in the database.
    // Returns a LiveData object for the UI to observe.
    fun getTotalTasks(): LiveData<Int> {
        Log.d(TAG, "Fetching total tasks count")
        return repository.getTotalTasks() // Delegates to the repository to fetch the total task count.
    }

    // Retrieves the number of completed tasks in the database.
    // Returns a LiveData object for the UI to observe.
    fun getCompletedTasks(): LiveData<Int> {
        Log.d(TAG, "Fetching completed tasks count")
        return repository.getCompletedTasks() // Delegates to the repository to fetch the completed task count.
    }

    // Retrieves the number of tasks completed today.
    // Returns a LiveData object for the UI to observe.
    fun getCompletedTasksToday(): LiveData<Int> {
        Log.d(TAG, "Fetching tasks completed today count")
        return repository.getCompletedTasksToday() // Delegates to the repository to fetch today's completed tasks.
    }

    // Retrieves the number of tasks due within the current week.
    // Returns a LiveData object for the UI to observe.
    fun getTasksDueThisWeek(): LiveData<Int> {
        Log.d(TAG, "Fetching tasks due this week count")
        return repository.getTasksDueThisWeek() // Delegates to the repository to fetch tasks due this week.
    }

    // Retrieves the number of high-priority tasks.
    // Transforms the LiveData<List<Task>> into LiveData<Int> by mapping the list size.
    fun getHighPriorityTasks(): LiveData<Int> {
        Log.d(TAG, "Fetching high priority tasks count")
        return repository.getTasksByPriority("High").switchMap { tasks ->
            val count = tasks.size
            Log.d(TAG, "High priority tasks count: $count")
            MutableLiveData(count) // Converts the list of high-priority tasks to its size.
        }
    }

    // Retrieves the number of medium-priority tasks.
    // Transforms the LiveData<List<Task>> into LiveData<Int> by mapping the list size.
    fun getMediumPriorityTasks(): LiveData<Int> {
        Log.d(TAG, "Fetching medium priority tasks count")
        return repository.getTasksByPriority("Medium").switchMap { tasks ->
            val count = tasks.size
            Log.d(TAG, "Medium priority tasks count: $count")
            MutableLiveData(count) // Converts the list of medium-priority tasks to its size.
        }
    }

    // Retrieves the number of low-priority tasks.
    // Transforms the LiveData<List<Task>> into LiveData<Int> by mapping the list size.
    fun getLowPriorityTasks(): LiveData<Int> {
        Log.d(TAG, "Fetching low priority tasks count")
        return repository.getTasksByPriority("Low").switchMap { tasks ->
            val count = tasks.size
            Log.d(TAG, "Low priority tasks count: $count")
            MutableLiveData(count) // Converts the list of low-priority tasks to its size.
        }
    }
}
