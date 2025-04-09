package com.example.assignment_3.ui

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

// AddEditScreen is a composable function that provides a form to add or edit a task.
// It takes a taskId (-1 for adding a new task, otherwise the ID of the task to edit),
// a TaskViewModel to manage task data, and a NavController for navigation.
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for experimental Material3 APIs (e.g., DatePicker).
@Composable
fun AddEditScreen(taskId: Int, viewModel: TaskViewModel, navController: NavController) {
    // State variables to hold form input values, preserved across configuration changes.
    var name by rememberSaveable { mutableStateOf("") } // Task name.
    var description by rememberSaveable { mutableStateOf("") } // Task description.
    var dueDateTime by rememberSaveable { mutableStateOf("") } // Due date and time in "dd/MM/yyyy HH:mm" format.
    var priority by rememberSaveable { mutableStateOf("Medium") } // Task priority, defaulting to "Medium".
    var isCompleted by rememberSaveable { mutableStateOf(false) } // Task completion status.
    var showDatePicker by rememberSaveable { mutableStateOf(false) } // Controls visibility of the date picker dialog.
    var showTimePicker by rememberSaveable { mutableStateOf(false) } // Controls visibility of the time picker dialog.
    var dateError by rememberSaveable { mutableStateOf<String?>(null) } // Holds error message for date validation.

    // Formatter for parsing and formatting date-time strings.
    val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    // Get the current date and time for validation purposes.
    val today = Calendar.getInstance()

    // LaunchedEffect to load task data when editing an existing task.
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
            }
        }
    }

    // Get the current device configuration to determine the screen orientation.
    val configuration = LocalConfiguration.current
    // Check if the device is in landscape mode to adjust the layout accordingly.
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
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
                    onValueChange = { name = it }, // Updates the name state when the user types.
                    label = { Text("Task Name", style = TextStyle(fontSize = 18.sp)) }, // Label with increased font size.
                    modifier = Modifier.fillMaxWidth(), // Fills the available width.
                    textStyle = TextStyle(fontSize = 20.sp) // Increased input text size.
                )

                // Text field for entering the task description.
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it }, // Updates the description state when the user types.
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
                            IconButton(onClick = { showDatePicker = true }) { // Opens the date picker dialog.
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                            IconButton(onClick = { showTimePicker = true }) { // Opens the time picker dialog.
                                Icon(Icons.Default.AccessTime, contentDescription = "Select Time")
                            }
                        }
                    },
                    isError = dateError != null, // Shows an error state if date validation fails.
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
                        onDismissRequest = { showDatePicker = false }, // Closes the dialog when dismissed.
                        confirmButton = {
                            TextButton(onClick = {
                                // Update the due date with the selected date, keeping the existing time.
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val currentTime = dueDateTime.split(" ").getOrNull(1) ?: "00:00"
                                    val newDateTime = dateTimeFormatter.format(Date(millis)) + " $currentTime"
                                    dueDateTime = newDateTime
                                    // Validate the new date-time and update the error state.
                                    dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
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
                        onDismissRequest = { showTimePicker = false }, // Closes the dialog when dismissed.
                        confirmButton = {
                            TextButton(onClick = {
                                // Update the due time with the selected time, keeping the existing date.
                                val datePart = dueDateTime.split(" ").getOrNull(0) ?: dateTimeFormatter.format(today.time)
                                val newDateTime = "$datePart ${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                                dueDateTime = newDateTime
                                // Validate the new date-time and update the error state.
                                dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
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
                            IconButton(onClick = { expanded = true }) { // Opens the dropdown menu.
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Priority")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }, // Closes the dropdown when dismissed.
                        modifier = Modifier.fillMaxWidth() // Makes the dropdown as wide as the text field.
                    ) {
                        // Populate the dropdown with priority options.
                        listOf("High", "Medium", "Low").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, style = TextStyle(fontSize = 18.sp)) }, // Dropdown item with increased font size.
                                onClick = {
                                    priority = option // Update the priority state.
                                    expanded = false // Close the dropdown.
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
                            onCheckedChange = { isCompleted = it } // Updates the completion status.
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
                            // Create a Task object with the form data.
                            val task = Task(
                                id = if (taskId == -1) 0 else taskId, // ID is 0 for new tasks (auto-generated by the database).
                                name = name,
                                description = description,
                                dueDate = dueDateTime,
                                priority = priority,
                                isCompleted = if (taskId == -1) false else isCompleted // New tasks are not completed by default.
                            )
                            if (taskId == -1) {
                                viewModel.insert(task) // Insert a new task.
                            } else {
                                viewModel.update(task) // Update an existing task.
                            }
                            navController.popBackStack() // Navigate back to the previous screen.
                        },
                        modifier = Modifier.weight(1f), // Takes equal space in the row.
                        enabled = name.isNotBlank() && dueDateTime.isNotBlank() && dateError == null // Enable only if form is valid.
                    ) {
                        Text("Save", style = TextStyle(fontSize = 18.sp)) // Save button with increased font size.
                    }

                    // Delete Button: Shown only when editing an existing task.
                    if (taskId != -1) {
                        Button(
                            onClick = {
                                // Find and delete the task with the given ID.
                                viewModel.allTasks.value?.find { it.id == taskId }?.let { task ->
                                    viewModel.delete(task)
                                }
                                navController.popBackStack() // Navigate back to the previous screen.
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
        // Portrait Mode: Display all form fields in a single column.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (taskId == -1) "Add Task" else "Edit Task",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp) // Custom headline style with increased font size.
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Task Name", style = TextStyle(fontSize = 18.sp)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 20.sp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", style = TextStyle(fontSize = 18.sp)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 20.sp)
            )

            OutlinedTextField(
                value = dueDateTime,
                onValueChange = { /* No-op, read-only */ },
                label = { Text("Due Date & Time (dd/MM/yyyy HH:mm)", style = TextStyle(fontSize = 18.sp)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                textStyle = TextStyle(fontSize = 20.sp),
                trailingIcon = {
                    Row {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Select Time")
                        }
                    }
                },
                isError = dateError != null,
                supportingText = { if (dateError != null) Text(dateError!!, style = TextStyle(fontSize = 16.sp)) }
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = dueDateTime.takeIf { it.isNotBlank() }
                        ?.let { dateTimeFormatter.parse(it)?.time } ?: today.timeInMillis,
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= today.timeInMillis - 24 * 60 * 60 * 1000
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val currentTime = dueDateTime.split(" ").getOrNull(1) ?: "00:00"
                                val newDateTime = dateTimeFormatter.format(Date(millis)) + " $currentTime"
                                dueDateTime = newDateTime
                                dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
                            }
                            showDatePicker = false
                        }) {
                            Text("OK", style = TextStyle(fontSize = 18.sp))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel", style = TextStyle(fontSize = 18.sp))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showTimePicker) {
                val timePickerState = rememberTimePickerState(
                    initialHour = dueDateTime.split(" ").getOrNull(1)?.split(":")?.get(0)?.toIntOrNull() ?: 0,
                    initialMinute = dueDateTime.split(" ").getOrNull(1)?.split(":")?.get(1)?.toIntOrNull() ?: 0,
                    is24Hour = true
                )
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val datePart = dueDateTime.split(" ").getOrNull(0) ?: dateTimeFormatter.format(today.time)
                            val newDateTime = "$datePart ${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                            dueDateTime = newDateTime
                            dateError = validateDateTime(newDateTime, today, dateTimeFormatter)
                            showTimePicker = false
                        }) {
                            Text("OK", style = TextStyle(fontSize = 18.sp))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel", style = TextStyle(fontSize = 18.sp))
                        }
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }

            var expanded by rememberSaveable { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = priority,
                    onValueChange = {},
                    label = { Text("Priority", style = TextStyle(fontSize = 18.sp)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    textStyle = TextStyle(fontSize = 20.sp),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Priority")
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("High", "Medium", "Low").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, style = TextStyle(fontSize = 18.sp)) },
                            onClick = {
                                priority = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (taskId != -1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                    Text("Completed", style = TextStyle(fontSize = 18.sp))
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val task = Task(
                            id = if (taskId == -1) 0 else taskId,
                            name = name,
                            description = description,
                            dueDate = dueDateTime,
                            priority = priority,
                            isCompleted = if (taskId == -1) false else isCompleted
                        )
                        if (taskId == -1) {
                            viewModel.insert(task)
                        } else {
                            viewModel.update(task)
                        }
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && dueDateTime.isNotBlank() && dateError == null
                ) {
                    Text("Save", style = TextStyle(fontSize = 18.sp))
                }

                if (taskId != -1) {
                    Button(
                        onClick = {
                            viewModel.allTasks.value?.find { it.id == taskId }?.let { task ->
                                viewModel.delete(task)
                            }
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete", style = TextStyle(fontSize = 18.sp))
                    }
                }
            }
        }
    }
}

// Validates the date-time string to ensure it is in the correct format and not in the past.
// Returns an error message if validation fails, or null if the date-time is valid.
private fun validateDateTime(dateTimeStr: String, today: Calendar, formatter: SimpleDateFormat): String? {
    if (dateTimeStr.isBlank()) return null // No validation if the string is blank.
    if (dateTimeStr.length != 16) return "Invalid format (use dd/MM/yyyy HH:mm)" // Check for correct length.
    return try {
        // Parse the date-time string.
        val dateTime = formatter.parse(dateTimeStr) ?: return "Invalid date/time"
        // Ensure the date-time is not in the past.
        if (dateTime.before(today.time)) return "Date and time cannot be in the past"
        null // No error if validation passes.
    } catch (e: Exception) {
        "Invalid date/time" // Return an error message if parsing fails.
    }
}

// TimePickerDialog is a custom composable that wraps a TimePicker in an AlertDialog.
// It takes callbacks for dismissal and confirmation, and the content to display (the TimePicker).
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit, // Callback invoked when the dialog is dismissed.
    confirmButton: @Composable () -> Unit, // Composable for the confirm button.
    dismissButton: @Composable () -> Unit, // Composable for the dismiss button.
    content: @Composable () -> Unit // The TimePicker content to display.
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { content() } // Display the TimePicker as the dialog's content.
    )
}