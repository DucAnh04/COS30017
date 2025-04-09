package com.example.assignment_3.ui.viewmodel

import android.app.Application
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
    }

    // Sets the current filter to the specified priority, triggering an update to filteredTasks.
    fun setFilter(priority: String) {
        _currentFilter.value = priority // Update the filter value, which will update filteredTasks via switchMap.
    }

    // Inserts a new task into the database.
    // Uses a coroutine to perform the operation on a background thread.
    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task) // Calls the repository's insert method to add the task to the database.
    }

    // Updates an existing task in the database.
    // Uses a coroutine to perform the operation on a background thread.
    fun update(task: Task) = viewModelScope.launch {
        repository.update(task) // Calls the repository's update method to modify the task in the database.
    }

    // Deletes a task from the database.
    // Uses a coroutine to perform the operation on a background thread.
    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task) // Calls the repository's delete method to remove the task from the database.
    }

    // Retrieves tasks filtered by the specified priority.
    // Returns a LiveData object for the UI to observe.
    fun getTasksByPriority(priority: String): LiveData<List<Task>> {
        return repository.getTasksByPriority(priority) // Delegates to the repository to fetch tasks by priority.
    }

    // Retrieves the total number of tasks in the database.
    // Returns a LiveData object for the UI to observe.
    fun getTotalTasks(): LiveData<Int> {
        return repository.getTotalTasks() // Delegates to the repository to fetch the total task count.
    }

    // Retrieves the number of completed tasks in the database.
    // Returns a LiveData object for the UI to observe.
    fun getCompletedTasks(): LiveData<Int> {
        return repository.getCompletedTasks() // Delegates to the repository to fetch the completed task count.
    }

    // Retrieves the number of tasks completed today.
    // Returns a LiveData object for the UI to observe.
    fun getCompletedTasksToday(): LiveData<Int> {
        return repository.getCompletedTasksToday() // Delegates to the repository to fetch today's completed tasks.
    }

    // Retrieves the number of tasks due within the current week.
    // Returns a LiveData object for the UI to observe.
    fun getTasksDueThisWeek(): LiveData<Int> {
        return repository.getTasksDueThisWeek() // Delegates to the repository to fetch tasks due this week.
    }

    // Retrieves the number of high-priority tasks.
    // Transforms the LiveData<List<Task>> into LiveData<Int> by mapping the list size.
    fun getHighPriorityTasks(): LiveData<Int> {
        return repository.getTasksByPriority("High").switchMap { tasks ->
            MutableLiveData(tasks.size) // Converts the list of high-priority tasks to its size.
        }
    }

    // Retrieves the number of medium-priority tasks.
    // Transforms the LiveData<List<Task>> into LiveData<Int> by mapping the list size.
    fun getMediumPriorityTasks(): LiveData<Int> {
        return repository.getTasksByPriority("Medium").switchMap { tasks ->
            MutableLiveData(tasks.size) // Converts the list of medium-priority tasks to its size.
        }
    }

    // Retrieves the number of low-priority tasks.
    // Transforms the LiveData<List<Task>> into LiveData<Int> by mapping the list size.
    fun getLowPriorityTasks(): LiveData<Int> {
        return repository.getTasksByPriority("Low").switchMap { tasks ->
            MutableLiveData(tasks.size) // Converts the list of low-priority tasks to its size.
        }
    }
}