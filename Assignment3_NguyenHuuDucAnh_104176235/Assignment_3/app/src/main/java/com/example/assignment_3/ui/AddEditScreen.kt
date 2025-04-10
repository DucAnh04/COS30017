package com.example.assignment_3.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment_3.data.Task
import com.example.assignment_3.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

private const val TAG = "AddEditScreen"  // Added TAG constant for logging

// Opt-in for experimental Material3 APIs (e.g., DatePicker, TimePicker).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(taskId: Int, viewModel: TaskViewModel, navController: NavController) {
    // Get the current context to show Toast messages.
    val context = LocalContext.current

    // State variables to hold form input values, preserved across configuration changes using rememberSaveable.
    var name by rememberSaveable { mutableStateOf("") } // Holds the task name input.
    var description by rememberSaveable { mutableStateOf("") } // Holds the task description input.
    var dueDateTime by rememberSaveable { mutableStateOf("") } // Holds the due date and time in "dd/MM/yyyy HH:mm" format.
    var priority by rememberSaveable { mutableStateOf("Medium") } // Holds the task priority, defaulting to "Medium".
    var isCompleted by rememberSaveable { mutableStateOf(false) } // Holds the task completion status (true if completed).
    var showDatePicker by rememberSaveable { mutableStateOf(false) } // Controls visibility of the date picker dialog.
    var showTimePicker by rememberSaveable { mutableStateOf(false) } // Controls visibility of the time picker dialog.
    var dateError by rememberSaveable { mutableStateOf<String?>(null) } // Holds error message for date validation (null if no error).
    // Tracks the state of actions (save, delete, validation error) to handle Toast messages and navigation.
    var actionState by remember { mutableStateOf<ActionState?>(null) }

    // Formatter for parsing and formatting date-time strings in the format "dd/MM/yyyy HH:mm".
    val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    // Get the current date and time for validation purposes (to ensure due date is not in the past).
    val today = Calendar.getInstance()

    // Log initial state
    Log.d(TAG, "Screen initialized with taskId: $taskId")

    // LaunchedEffect to load task data when editing an existing task.
    // This runs whenever taskId changes.
    LaunchedEffect(taskId) {
        if (taskId != -1) { // If taskId is not -1, we're editing an existing task.
            // Find the task with the given ID from the ViewModel's allTasks LiveData.
            viewModel.allTasks.value?.find { it.id == taskId }?.let { task ->
                // Populate the form fields with the task's existing data.
                name = task.name
                description = task.description
                dueDateTime = task.dueDate
                priority = task.priority
                isCompleted = task.isCompleted
                Log.d(TAG, "Loaded task data: ${task.toString()}")
            } ?: Log.d(TAG, "No task found for id: $taskId")
        }
    }

    // Handle Toast messages and navigation after successful database operations.
    // This LaunchedEffect runs whenever actionState changes.
    LaunchedEffect(actionState) {
        when (actionState) {
            is ActionState.SaveSuccess -> {
                // Show a Toast confirming the task was saved successfully.
                Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Save successful, navigating back")
                // Navigate back to the TaskListScreen after showing the Toast.
                navController.popBackStack()
            }
            is ActionState.DeleteSuccess -> {
                // Show a Toast confirming the task was deleted.
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Delete successful, navigating back")
                // Navigate back to the TaskListScreen after showing the Toast.
                navController.popBackStack()
            }
            is ActionState.ValidationError -> {
                // Show a Toast with the validation error message (e.g., "Due date cannot be in the past").
                Toast.makeText(context, actionState!!.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Validation error: ${actionState!!.message}")
            }
            null -> {} // No action to take if action TraumaticState is null.
        }
    }

    // Get the current device configuration to determine the screen orientation.
    val configuration = LocalConfiguration.current
    // Check if the device is in landscape mode to adjust the layout (two-column layout in landscape).
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Log.d(TAG, "Rendering in landscape mode")
        // Landscape Mode: Split the form into two columns for better space utilization.
        Row(
            modifier = Modifier
                .fillMaxSize() // Fills the entire available space.
                .padding(16.dp), // Adds 16dp padding around the content.
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Adds 16dp spacing between the two columns.
        ) {
            // Left Column: Contains the task name and description fields.
            Column(
                modifier = Modifier
                    .weight(1f) // Takes equal space in the row.
                    .fillMaxHeight(), // Fills the available height.
                verticalArrangement = Arrangement.spacedBy(16.dp) // Adds 16dp spacing between items.
            ) {
                // Display the screen title based on whether we're adding or editing a task.
                Text(
                    text = if (taskId == -1) "Add Task" else "Edit Task",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp) // Custom headline style with increased font size.
                )

                // Text field for entering the task name.
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it // Updates the name state when the user types.
                        Log.d(TAG, "Name changed to: $it")
                    },
                    label = { Text("Task Name", style = TextStyle(fontSize = 18.sp)) }, // Label with increased font size.
                    modifier = Modifier.fillMaxWidth(), // Fills the available width.
                    textStyle = TextStyle(fontSize = 20.sp) // Increased input text size.
                )

                // Text field for entering the task description.
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it // Updates the description state when the user types.
                        Log.d(TAG, "Description changed to: $it")
                    },
                    label = { Text("Description", style = TextStyle(fontSize = 18.sp)) }, // Label with increased font size.
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 20.sp) // Increased input text size.
                )
            }

            // Right Column: Contains the due date, priority, completed status, and action buttons.
            Column(
                modifier = Modifier
                    .weight(1f) // Takes equal space in the row.
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Due Date-Time Field: Read-only field with icons to open date and time pickers.
                OutlinedTextField(
                    value = dueDateTime,
                    onValueChange = { /* No-op, read-only */ }, // Field is read-only; user must use pickers to change the value.
                    label = { Text("Due Date & Time (dd/MM/yyyy HH:mm)", style = TextStyle(fontSize = 18.sp)) }, // Label with format hint.
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    textStyle = TextStyle(fontSize = 20.sp), // Increased input text size.
                    trailingIcon = {
                        // Row containing icons for date and time pickers.
                        Row {
                            IconButton(onClick = {
                                showDatePicker = true // Opens the date picker dialog.
                                Log.d(TAG, "Date picker opened")
                            }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                            IconButton(onClick = {
                                showTimePicker = true // Opens the time picker dialog.
                                Log.d(TAG, "Time picker opened")
                            }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Select Time")
                            }
                        }
                    },
                    isError = dateError != null, // Shows an error state (red underline) if date validation fails.
                    supportingText = { if (dateError != null) Text(dateError!!, style = TextStyle(fontSize = 16.sp)) } // Displays error message below the field.
                )

                // Date Picker Dialog: Shown when showDatePicker is true.
                if (showDatePicker) {
                    // Initialize the date picker state with the current due date or today's date.
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = dueDateTime.takeIf { it.isNotBlank() }
                            ?.let { dateTimeFormatter.parse(it)?.time } ?: today.timeInMillis,
                        selectableDates = object : SelectableDates {
                            // Restrict selectable dates to today or future dates.
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                return utcTimeMillis >= today.timeInMillis - 24 * 60 * 60 * 1000
                            }
                        }
                    )
                    DatePickerDialog(
                        onDismissRequest = {
                            showDatePicker = false // Closes the dialog when dismissed.
                            Log.d(TAG, "Date picker dismissed")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                // Update the due date with the selected date, keeping the existing time.
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val currentTime = dueDateTime.split(" ").getOrNull(1) ?: "00:00"
                                    val newDateTime = dateTimeFormatter.format(Date(millis)) + " $currentTime"
                                    dueDateTime = newDateTime
                                    // Validate the new date-time and update the error state.
                                    dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
                                    Log.d(TAG, "Date selected: $newDateTime, error: $dateError")
                                    // If the date is in the past, show a Toast message.
                                    if (dateError == "Date and time cannot be in the past") {
                                        actionState = ActionState.ValidationError("Due date cannot be in the past")
                                    }
                                }
                                showDatePicker = false // Close the dialog.
                            }) {
                                Text("OK", style = TextStyle(fontSize = 18.sp)) // Confirm button with increased font size.
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel", style = TextStyle(fontSize = 18.sp)) // Cancel button with increased font size.
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState) // Display the date picker.
                    }
                }

                // Time Picker Dialog: Shown when showTimePicker is true.
                if (showTimePicker) {
                    // Initialize the time picker state with the current due time or default to 00:00.
                    val timePickerState = rememberTimePickerState(
                        initialHour = dueDateTime.split(" ").getOrNull(1)?.split(":")?.get(0)?.toIntOrNull() ?: 0,
                        initialMinute = dueDateTime.split(" ").getOrNull(1)?.split(":")?.get(1)?.toIntOrNull() ?: 0,
                        is24Hour = true // Use 24-hour format.
                    )
                    TimePickerDialog(
                        onDismissRequest = {
                            showTimePicker = false // Closes the dialog when dismissed.
                            Log.d(TAG, "Time picker dismissed")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                // Update the due time with the selected time, keeping the existing date.
                                val datePart = dueDateTime.split(" ").getOrNull(0) ?: dateTimeFormatter.format(today.time)
                                val newDateTime = "$datePart ${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                                dueDateTime = newDateTime
                                // Validate the new date-time and update the error state.
                                dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
                                Log.d(TAG, "Time selected: $newDateTime, error: $dateError")
                                // If the date is in the past, show a Toast message.
                                if (dateError == "Date and time cannot be in the past") {
                                    actionState = ActionState.ValidationError("Due date cannot be in the past")
                                }
                                showTimePicker = false // Close the dialog.
                            }) {
                                Text("OK", style = TextStyle(fontSize = 18.sp)) // Confirm button with increased font size.
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) {
                                Text("Cancel", style = TextStyle(fontSize = 18.sp)) // Cancel button with increased font size.
                            }
                        }
                    ) {
                        TimePicker(state = timePickerState) // Display the time picker.
                    }
                }

                // Priority Dropdown: Allows the user to select a priority level.
                var expanded by rememberSaveable { mutableStateOf(false) } // Controls the visibility of the dropdown menu.
                Box {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {}, // Read-only; user must use the dropdown to change the value.
                        label = { Text("Priority", style = TextStyle(fontSize = 18.sp)) }, // Label with increased font size.
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        textStyle = TextStyle(fontSize = 20.sp), // Increased input text size.
                        trailingIcon = {
                            IconButton(onClick = {
                                expanded = true // Opens the dropdown menu.
                                Log.d(TAG, "Priority dropdown opened")
                            }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Priority")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false // Closes the dropdown when dismissed.
                            Log.d(TAG, "Priority dropdown dismissed")
                        },
                        modifier = Modifier.fillMaxWidth() // Makes the dropdown as wide as the text field.
                    ) {
                        // Populate the dropdown with priority options.
                        listOf("High", "Medium", "Low").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, style = TextStyle(fontSize = 18.sp)) }, // Dropdown item with increased font size.
                                onClick = {
                                    priority = option // Update the priority state.
                                    expanded = false // Close the dropdown.
                                    Log.d(TAG, "Priority selected: $option")
                                }
                            )
                        }
                    }
                }

                // Completed Checkbox: Shown only when editing an existing task.
                if (taskId != -1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = {
                                isCompleted = it // Updates the completion status.
                                Log.d(TAG, "Completed status changed to: $it")
                            }
                        )
                        Text("Completed", style = TextStyle(fontSize = 18.sp)) // Checkbox label with increased font size.
                    }
                }

                // Action Buttons: Save and (if editing) Delete.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // Adds 16dp spacing between buttons.
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            // Validate inputs before proceeding with the save operation.
                            if (name.isBlank()) {
                                // If task name is empty, show a validation error Toast.
                                actionState = ActionState.ValidationError("Task name cannot be empty")
                                return@Button
                            }
                            if (dueDateTime.isBlank()) {
                                // If due date is empty, show a validation error Toast.
                                actionState = ActionState.ValidationError("Due date cannot be empty")
                                return@Button
                            }
                            if (dateError != null) {
                                // If there's a date error (e.g., date in the past), show a validation error Toast.
                                actionState = ActionState.ValidationError("Due date cannot be in the past")
                                return@Button
                            }

                            // Create a Task object with the form data.
                            val task = Task(
                                id = if (taskId == -1) 0 else taskId, // ID is 0 for new tasks (auto-generated by Room).
                                name = name,
                                description = description,
                                dueDate = dueDateTime,
                                priority = priority,
                                isCompleted = if (taskId == -1) false else isCompleted // New tasks are not completed by default.
                            )
                            Log.d(TAG, "Saving task: ${task.toString()}")
                            if (taskId == -1) {
                                // Insert a new task into the database.
                                viewModel.insert(task)
                                // Set actionState to trigger a success Toast and navigation.
                                actionState = ActionState.SaveSuccess
                            } else {
                                // Update an existing task in the database.
                                viewModel.update(task)
                                // Set actionState to trigger a success Toast and navigation.
                                actionState = ActionState.SaveSuccess
                            }
                        },
                        modifier = Modifier.weight(1f), // Takes equal space in the row.
                        // Enable the button only if all validations pass.
                        enabled = name.isNotBlank() && dueDateTime.isNotBlank() && dateError == null
                    ) {
                        Text("Save", style = TextStyle(fontSize = 18.sp)) // Save button with increased font size.
                    }

                    // Delete Button: Shown only when editing an existing task.
                    if (taskId != -1) {
                        Button(
                            onClick = {
                                // Find the task with the given ID and delete it.
                                viewModel.allTasks.value?.find { it.id == taskId }?.let { task ->
                                    Log.d(TAG, "Deleting task: ${task.toString()}")
                                    viewModel.delete(task)
                                    // Set actionState to trigger a success Toast and navigation.
                                    actionState = ActionState.DeleteSuccess
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Red color to indicate a destructive action.
                        ) {
                            Text("Delete", style = TextStyle(fontSize = 18.sp)) // Delete button with increased font size.
                        }
                    }
                }
            }
        }
    } else {
        Log.d(TAG, "Rendering in portrait mode")
        // Portrait Mode: Display all form fields in a single column for a compact layout.
        Column(
            modifier = Modifier
                .fillMaxSize() // Fills the entire available screen space.
                .padding(16.dp), // Adds 16dp padding around the content for consistent spacing.
            verticalArrangement = Arrangement.spacedBy(16.dp) // Adds 16dp spacing between child elements vertically.
        ) {
            // Screen Title: Displays "Add Task" for new tasks or "Edit Task" for existing tasks.
            Text(
                text = if (taskId == -1) "Add Task" else "Edit Task",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp) // Custom headline style with increased font size for better readability.
            )

            // Task Name Field: Allows the user to input the task name.
            OutlinedTextField(
                value = name, // Binds the text field to the name state variable.
                onValueChange = {
                    name = it // Updates the name state when the user types.
                    Log.d(TAG, "Name changed to: $it")
                },
                label = { Text("Task Name", style = TextStyle(fontSize = 18.sp)) }, // Label with increased font size for clarity.
                modifier = Modifier.fillMaxWidth(), // Fills the available width of the column.
                textStyle = TextStyle(fontSize = 20.sp) // Increased input text size for better readability.
            )

            // Description Field: Allows the user to input an optional task description.
            OutlinedTextField(
                value = description, // Binds the text field to the description state variable.
                onValueChange = {
                    description = it // Updates the description state when the user types.
                    Log.d(TAG, "Description changed to: $it")
                },
                label = { Text("Description", style = TextStyle(fontSize = 18.sp)) }, // Label with increased font size.
                modifier = Modifier.fillMaxWidth(), // Fills the available width of the column.
                textStyle = TextStyle(fontSize = 20.sp) // Increased input text size.
            )

            // Due Date-Time Field: Read-only field for selecting the due date and time using pickers.
            OutlinedTextField(
                value = dueDateTime, // Binds the text field to the dueDateTime state variable.
                onValueChange = { /* No-op, read-only */ }, // Field is read-only; user must use date/time pickers to change the value.
                label = { Text("Due Date & Time (dd/MM/yyyy HH:mm)", style = TextStyle(fontSize = 18.sp)) }, // Label with format hint for user guidance.
                modifier = Modifier.fillMaxWidth(), // Fills the available width.
                readOnly = true, // Prevents direct text input.
                textStyle = TextStyle(fontSize = 20.sp), // Increased input text size.
                trailingIcon = {
                    // Row containing icons for date and time pickers.
                    Row {
                        IconButton(onClick = {
                            showDatePicker = true // Opens the date picker dialog when clicked.
                            Log.d(TAG, "Date picker opened")
                        }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date") // Calendar icon for date selection.
                        }
                        IconButton(onClick = {
                            showTimePicker = true // Opens the time picker dialog when clicked.
                            Log.d(TAG, "Time picker opened")
                        }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Select Time") // Clock icon for time selection.
                        }
                    }
                },
                isError = dateError != null, // Shows an error state (red underline) if date validation fails.
                supportingText = { if (dateError != null) Text(dateError!!, style = TextStyle(fontSize = 16.sp)) } // Displays error message below the field if validation fails.
            )

            // Date Picker Dialog: Shown when showDatePicker is true, allowing the user to select a date.
            if (showDatePicker) {
                // Initialize the date picker state with the current due date (if set) or today's date.
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = dueDateTime.takeIf { it.isNotBlank() }
                        ?.let { dateTimeFormatter.parse(it)?.time } ?: today.timeInMillis, // Sets initial date to dueDateTime or current date.
                    selectableDates = object : SelectableDates {
                        // Restrict selectable dates to today or future dates to prevent past dates.
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= today.timeInMillis - 24 * 60 * 60 * 1000
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = {
                        showDatePicker = false // Closes the dialog when dismissed (e.g., back button).
                        Log.d(TAG, "Date picker dismissed")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            // Update the due date with the selected date, keeping the existing time.
                            datePickerState.selectedDateMillis?.let { millis ->
                                val currentTime = dueDateTime.split(" ").getOrNull(1) ?: "00:00" // Default to "00:00" if no time is set.
                                val newDateTime = dateTimeFormatter.format(Date(millis)) + " $currentTime" // Combine new date with existing time.
                                dueDateTime = newDateTime // Update the dueDateTime state.
                                // Validate the new date-time and update the error state.
                                dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
                                Log.d(TAG, "Date selected: $newDateTime, error: $dateError")
                                // If the date is in the past, show a Toast message.
                                if (dateError == "Date and time cannot be in the past") {
                                    actionState = ActionState.ValidationError("Due date cannot be in the past")
                                }
                            }
                            showDatePicker = false // Close the dialog.
                        }) {
                            Text("OK", style = TextStyle(fontSize = 18.sp)) // Confirm button with increased font size.
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { // Closes the dialog without saving changes.
                            Text("Cancel", style = TextStyle(fontSize = 18.sp)) // Cancel button with increased font size.
                        }
                    }
                ) {
                    DatePicker(state = datePickerState) // Display the date picker UI.
                }
            }

            // Time Picker Dialog: Shown when showTimePicker is true, allowing the user to select a time.
            if (showTimePicker) {
                // Initialize the time picker state with the current due time (if set) or default to 00:00.
                val timePickerState = rememberTimePickerState(
                    initialHour = dueDateTime.split(" ").getOrNull(1)?.split(":")?.get(0)?.toIntOrNull() ?: 0, // Extracts hour from dueDateTime or defaults to 0.
                    initialMinute = dueDateTime.split(" ").getOrNull(1)?.split(":")?.get(1)?.toIntOrNull() ?: 0, // Extracts minute from dueDateTime or defaults to 0.
                    is24Hour = true // Uses 24-hour format for consistency.
                )
                TimePickerDialog(
                    onDismissRequest = {
                        showTimePicker = false // Closes the dialog when dismissed.
                        Log.d(TAG, "Time picker dismissed")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            // Update the due time with the selected time, keeping the existing date.
                            val datePart = dueDateTime.split(" ").getOrNull(0) ?: dateTimeFormatter.format(today.time) // Uses existing date or current date if none set.
                            val newDateTime = "$datePart ${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}" // Combines date with new time.
                            dueDateTime = newDateTime // Update the dueDateTime state.
                            // Validate the new date-time and update the error state.
                            dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
                            Log.d(TAG, "Time selected: $newDateTime, error: $dateError")
                            // If the date is in the past, show a Toast message.
                            if (dateError == "Date and time cannot be in the past") {
                                actionState = ActionState.ValidationError("Due date cannot be in the past")
                            }
                            showTimePicker = false // Close the dialog.
                        }) {
                            Text("OK", style = TextStyle(fontSize = 18.sp)) // Confirm button with increased font size.
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) { // Closes the dialog without saving changes.
                            Text("Cancel", style = TextStyle(fontSize = 18.sp)) // Cancel button with increased font size.
                        }
                    }
                ) {
                    TimePicker(state = timePickerState) // Display the time picker UI.
                }
            }

            // Priority Dropdown: Allows the user to select a priority level for the task.
            var expanded by rememberSaveable { mutableStateOf(false) } // Controls the visibility of the dropdown menu.
            Box {
                OutlinedTextField(
                    value = priority, // Binds the text field to the priority state variable.
                    onValueChange = {}, // Read-only; user must use the dropdown to change the value.
                    label = { Text("Priority", style = TextStyle(fontSize = 18.sp)) }, // Label with increased font size.
                    modifier = Modifier.fillMaxWidth(), // Fills the available width.
                    readOnly = true, // Prevents direct text input.
                    textStyle = TextStyle(fontSize = 20.sp), // Increased input text size.
                    trailingIcon = {
                        IconButton(onClick = {
                            expanded = true // Opens the dropdown menu when clicked.
                            Log.d(TAG, "Priority dropdown opened")
                        }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Priority") // Dropdown arrow icon.
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded, // Shows the dropdown when expanded is true.
                    onDismissRequest = {
                        expanded = false // Closes the dropdown when dismissed.
                        Log.d(TAG, "Priority dropdown dismissed")
                    },
                    modifier = Modifier.fillMaxWidth() // Makes the dropdown as wide as the text field.
                ) {
                    // Populate the dropdown with priority options: High, Medium, Low.
                    listOf("High", "Medium", "Low").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, style = TextStyle(fontSize = 18.sp)) }, // Dropdown item with increased font size.
                            onClick = {
                                priority = option // Update the priority state with the selected option.
                                expanded = false // Close the dropdown.
                                Log.d(TAG, "Priority selected: $option")
                            }
                        )
                    }
                }
            }

            // Completed Checkbox: Shown only when editing an existing task to allow marking it as completed.
            if (taskId != -1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCompleted, // Binds the checkbox to the isCompleted state variable.
                        onCheckedChange = {
                            isCompleted = it // Updates the completion status when the checkbox is toggled.
                            Log.d(TAG, "Completed status changed to: $it")
                        }
                    )
                    Text("Completed", style = TextStyle(fontSize = 18.sp)) // Checkbox label with increased font size.
                }
            }

            // Action Buttons: Save and (if editing) Delete, displayed in a row.
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Adds 16dp spacing between buttons.
                modifier = Modifier.fillMaxWidth() // Fills the available width of the column.
            ) {
                Button(
                    onClick = {
                        // Validate inputs before proceeding with the save operation.
                        if (name.isBlank()) {
                            // If task name is empty, show a validation error Toast.
                            actionState = ActionState.ValidationError("Task name cannot be empty")
                            return@Button
                        }
                        if (dueDateTime.isBlank()) {
                            // If due date is empty, show a validation error Toast.
                            actionState = ActionState.ValidationError("Due date cannot be empty")
                            return@Button
                        }
                        if (dateError != null) {
                            // If there's a date error (e.g., date in the past), show a validation error Toast.
                            actionState = ActionState.ValidationError("Due date cannot be in the past")
                            return@Button
                        }

                        // Create a Task object with the form data.
                        val task = Task(
                            id = if (taskId == -1) 0 else taskId, // ID is 0 for new tasks (auto-generated by Room).
                            name = name,
                            description = description,
                            dueDate = dueDateTime,
                            priority = priority,
                            isCompleted = if (taskId == -1) false else isCompleted // New tasks are not completed by default.
                        )
                        Log.d(TAG, "Saving task: ${task.toString()}")
                        if (taskId == -1) {
                            // Insert a new task into the database.
                            viewModel.insert(task)
                            // Set actionState to trigger a success Toast and navigation.
                            actionState = ActionState.SaveSuccess
                        } else {
                            // Update an existing task in the database.
                            viewModel.update(task)
                            // Set actionState to trigger a success Toast and navigation.
                            actionState = ActionState.SaveSuccess
                        }
                    },
                    modifier = Modifier.weight(1f), // Takes equal space in the row.
                    // Enable the button only if all validations pass (non-empty name, non-empty due date, no date errors).
                    enabled = name.isNotBlank() && dueDateTime.isNotBlank() && dateError == null
                ) {
                    Text("Save", style = TextStyle(fontSize = 18.sp)) // Save button with increased font size.
                }

                // Delete Button: Shown only when editing an existing task.
                if (taskId != -1) {
                    Button(
                        onClick = {
                            // Find the task with the given ID and delete it from the database.
                            viewModel.allTasks.value?.find { it.id == taskId }?.let { task ->
                                Log.d(TAG, "Deleting task: ${task.toString()}")
                                viewModel.delete(task)
                                // Set actionState to trigger a success Toast and navigation.
                                actionState = ActionState.DeleteSuccess
                            }
                        },
                        modifier = Modifier.weight(1f), // Takes equal space in the row.
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Red color to indicate a destructive action.
                    ) {
                        Text("Delete", style = TextStyle(fontSize = 18.sp)) // Delete button with increased font size.
                    }
                }
            }
        }
    }
}

// Validates the date-time string to ensure it is in the correct format and not in the past.
// Parameters:
// - dateTimeStr: The date-time string to validate (e.g., "01/01/2025 12:00").
// - today: The current date and time for comparison.
// - formatter: The SimpleDateFormat used to parse the date-time string.
// Returns:
// - An error message (String) if validation fails, or null if the date-time is valid.
private fun validateDateTime(dateTimeStr: String, today: Calendar, formatter: SimpleDateFormat): String? {
    if (dateTimeStr.isBlank()) return null // No validation if the string is blank.
    if (dateTimeStr.length != 16) return "Invalid format (use dd/MM/yyyy HH:mm)" // Check for correct length (16 characters for "dd/MM/yyyy HH:mm").
    return try {
        // Parse the date-time string into a Date object.
        val dateTime = formatter.parse(dateTimeStr) ?: return "Invalid date/time"
        // Ensure the date-time is not in the past by comparing it with the current date.
        if (dateTime.before(today.time)) return "Date and time cannot be in the past"
        null // Return null if validation passes (no error).
    } catch (e: Exception) {
        "Invalid date/time" // Return an error message if parsing fails (e.g., malformed date).
    }
}

// TimePickerDialog is a custom composable that wraps a TimePicker in an AlertDialog.
// Parameters:
// - onDismissRequest: Callback invoked when the dialog is dismissed (e.g., user presses back).
// - confirmButton: Composable for the confirm button (e.g., "OK").
// - dismissButton: Composable for the dismiss button (e.g., "Cancel").
// - content: The TimePicker content to display inside the dialog.
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { content() } // Display the TimePicker as the dialog's content.
    )
}

// Sealed class to handle different action states for showing Toast messages and navigation.
// Each state represents a specific action or error condition.
sealed class ActionState(val message: String? = null) {
    // Indicates a successful save operation (insert or update).
    object SaveSuccess : ActionState()
    // Indicates a successful delete operation.
    object DeleteSuccess : ActionState()
    // Indicates a validation error, with a message to display in a Toast.
    class ValidationError(message: String) : ActionState(message)
}
