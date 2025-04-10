package com.example.assignment_3.ui.fragments

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment_3.R
import com.example.assignment_3.data.Task
import com.example.assignment_3.ui.viewmodel.TaskViewModel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import android.content.res.Configuration

private const val TAG = "TaskListScreen"  // Added TAG constant for logging

// TaskListScreen is a composable function that displays a list of tasks with a floating action button to add new tasks.
// It takes a TaskViewModel to fetch and observe tasks, a callback for task clicks, and a callback for adding a new task.
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit, // Callback invoked when a task item is clicked.
    onAddTask: () -> Unit // Callback invoked when the "Add Task" button is clicked.
) {
    // Observe the filtered tasks from the ViewModel as a LiveData object.
    // Initialize with an empty list to avoid null issues during initial composition.
    val tasks by viewModel.filteredTasks.observeAsState(initial = emptyList())

    // Get the current device configuration to determine the screen orientation.
    val configuration = LocalConfiguration.current
    // Check if the device is in landscape mode to adjust the layout accordingly.
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Log initial state
    Log.d(TAG, "Screen initialized, isLandscape: $isLandscape, task count: ${tasks.size}")

    // Use a Box to position the FloatingActionButton over the task list.
    Box(
        modifier = Modifier
            .fillMaxSize() // Fills the entire available space.
            .padding(16.dp) // Adds 16dp padding around the content for spacing.
    ) {
        // Main layout: a Column that contains the title and the task list.
        Column(
            modifier = Modifier.fillMaxSize() // Fills the entire available space within the Box.
        ) {
            // Display the screen title with a predefined typography style.
            Text(
                text = "Task List",
                style = MaterialTheme.typography.headlineSmall, // Uses a small headline style for the title.
                modifier = Modifier.padding(bottom = 16.dp) // Adds 16dp padding below the title.
            )

            // Use LazyColumn for efficient scrolling of the task list.
            LazyColumn(
                modifier = Modifier.weight(1f), // Takes remaining space, allowing the FAB to stay at the bottom.
                verticalArrangement = Arrangement.spacedBy(8.dp) // Adds 8dp vertical spacing between task items.
            ) {
                // Populate the LazyColumn with task items.
                items(tasks) { task ->
                    // Render each task as a clickable TaskItem.
                    TaskItem(
                        task = task,
                        onClick = {
                            onTaskClick(task) // Pass the task to the click callback.
                            Log.d(TAG, "Task clicked: ${task.name}, id: ${task.id}")
                        },
                        isLandscape = isLandscape, // Pass the orientation to adjust the layout.
                        isOverdue = viewModel.isTaskOverdue(task) // Pass overdue status
                    )
                }
            }
        }

        // FloatingActionButton to add a new task.
        FloatingActionButton(
            onClick = {
                onAddTask() // Invokes the onAddTask callback when clicked.
                Log.d(TAG, "Add Task FAB clicked")
            },
            modifier = Modifier
                .align(if (isLandscape) Alignment.BottomEnd else Alignment.BottomEnd) // Aligns the FAB to the bottom-right corner.
                .padding(
                    bottom = if (isLandscape) 16.dp else 16.dp, // Adds 16dp padding from the bottom.
                    end = if (isLandscape) 16.dp else 16.dp // Adds 16dp padding from the right.
                )
        ) {
            // Display an "Add" icon inside the FAB.
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }
}

// TaskItem is a composable function that displays a single task in a card format.
// It takes a Task object, a click callback, a boolean indicating if the device is in landscape mode, and a boolean indicating if the task is overdue.
@Composable
fun TaskItem(task: Task, onClick: () -> Unit, isLandscape: Boolean, isOverdue: Boolean) {
    // Load custom colors from resources for consistent theming of priority indicators.
    val darkRed = colorResource(id = R.color.dark_red) // Color for high priority tasks.
    val orange = colorResource(id = R.color.orange) // Color for medium priority tasks.
    val darkGreen = colorResource(id = R.color.dark_green) // Color for low priority tasks.
    val lightRed = colorResource(id = R.color.light_red) // Color for overdue tasks background

    // Log task rendering
    Log.d(TAG, "Rendering TaskItem: ${task.name}, priority: ${task.priority}, completed: ${task.isCompleted}, overdue: $isOverdue")

    // Use a Card to display the task with a clickable area.
    Card(
        modifier = Modifier
            .fillMaxWidth() // Fills the available width.
            .clickable { onClick() } // Makes the entire card clickable, invoking the onClick callback.
            .heightIn(min = if (isLandscape) 80.dp else 100.dp), // Sets a minimum height, shorter in landscape mode.
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Adds a 4dp elevation for a shadow effect.
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue && !task.isCompleted) lightRed else MaterialTheme.colorScheme.surface // Light red background if overdue and not completed
        )
    ) {
        // Use a Row to layout the task's icon and details side by side.
        Row(
            modifier = Modifier
                .padding(if (isLandscape) 16.dp else 24.dp) // Adjusts padding: 16dp in landscape, 24dp in portrait.
                .fillMaxWidth(), // Fills the width of the card.
            verticalAlignment = Alignment.CenterVertically // Aligns the icon and text vertically.
        ) {
            // Display an icon indicating the task's completion status.
            Icon(
                painter = painterResource(
                    if (task.isCompleted) R.drawable.ic_completed else R.drawable.ic_incomplete // Chooses the icon based on completion status.
                ),
                contentDescription = "Completion Status", // Accessibility description for the icon.
                modifier = Modifier.size(if (isLandscape) 28.dp else 32.dp) // Adjusts icon size: 28dp in landscape, 32dp in portrait.
            )
            Spacer(modifier = Modifier.width(if (isLandscape) 16.dp else 24.dp)) // Adds spacing between the icon and text: 16dp in landscape, 24dp in portrait.

            // Use a Column to stack the task's name, due date, and priority vertically.
            Column {
                // Display the task's name with a custom text style.
                Text(
                    text = task.name,
                    style = TextStyle(
                        fontSize = if (isLandscape) 28.sp else 26.sp, // Adjusts font size: 28sp in landscape, 26sp in portrait.
                        color = Color(0xFF1976D2) // Sets the text color to a shade of blue.
                    )
                )
                // Display the task's due date.
                Text(
                    text = "Due: ${task.dueDate}",
                    style = TextStyle(
                        fontSize = if (isLandscape) 18.sp else 20.sp, // Adjusts font size: 18sp in landscape, 20sp in portrait.
                    ),
                    color = if (isOverdue && !task.isCompleted) darkRed else Color.DarkGray, // Dark red if overdue and not completed
                    modifier = Modifier.padding(top = 4.dp) // Adds 4dp padding above the text.
                )
                // Display the task's priority with a color based on the priority level.
                Text(
                    text = "Priority: ${task.priority}",
                    style = TextStyle(
                        fontSize = if (isLandscape) 18.sp else 20.sp, // Adjusts font size: 18sp in landscape, 20sp in portrait.
                    ),
                    color = when (task.priority) {
                        "High" -> darkRed // Red for high priority.
                        "Medium" -> orange // Orange for medium priority.
                        "Low" -> darkGreen // Green for low priority.
                        else -> Color.Black // Default to black for unknown priorities.
                    },
                    modifier = Modifier.padding(top = 4.dp) // Adds 4dp padding above the text.
                )
            }
        }
    }
}
