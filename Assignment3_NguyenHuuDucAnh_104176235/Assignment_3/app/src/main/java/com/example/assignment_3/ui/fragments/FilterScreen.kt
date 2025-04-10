package com.example.assignment_3.ui.fragments

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import com.example.assignment_3.ui.viewmodel.TaskViewModel

private const val TAG = "FilterScreen"  // Added TAG constant for logging

// FilterScreen is a composable function that displays a UI for filtering tasks by priority.
// It takes a TaskViewModel to manage filter state and a callback to notify when a filter is selected.
@Composable
fun FilterScreen(viewModel: TaskViewModel, onFilterSelected: (String) -> Unit) {
    // Tracks the currently selected priority filter, defaulting to "All".
    var selectedPriority by remember { mutableStateOf("All") }
    // Controls whether the dropdown menu is expanded or collapsed.
    var expanded by remember { mutableStateOf(false) }

    // Retrieves the current device configuration to detect orientation.
    val configuration = LocalConfiguration.current
    // Determines if the device is in landscape mode for layout adjustments.
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Log initial state
    Log.d(TAG, "Screen initialized, isLandscape: $isLandscape, initial filter: $selectedPriority")

    // Main layout: a column that fills the screen with padding and spaced elements.
    Column(
        modifier = Modifier
            .fillMaxSize() // Takes up the full available size.
            .padding(16.dp), // Adds 16dp padding around the content.
        verticalArrangement = Arrangement.spacedBy(16.dp), // Spaces children vertically by 16dp.
        horizontalAlignment = if (isLandscape) androidx.compose.ui.Alignment.CenterHorizontally else androidx.compose.ui.Alignment.Start // Centers content in landscape, aligns start in portrait.
    ) {
        // Displays the screen title with a predefined typography style.
        Text(
            text = "Filter Tasks",
            style = MaterialTheme.typography.headlineSmall
        )

        // Container for the dropdown input field, adjusting width based on orientation.
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.5f else 1f) // Uses 50% width in landscape, full width in portrait.
        ) {
            // Text field to display the selected priority, styled as a dropdown trigger.
            OutlinedTextField(
                value = selectedPriority, // Shows the current filter value.
                onValueChange = {}, // Empty since the field is read-only (selection via dropdown).
                label = { Text("Filter by Priority", style = MaterialTheme.typography.bodyLarge) }, // Label for the field.
                modifier = Modifier.fillMaxWidth(), // Fills the available width of the Box.
                readOnly = true, // Prevents manual text input.
                trailingIcon = {
                    // Button to toggle the dropdown menu visibility.
                    IconButton(onClick = {
                        expanded = true
                        Log.d(TAG, "Dropdown opened")
                    }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Priority") // Arrow icon for dropdown.
                    }
                }
            )
            // Dropdown menu that appears when the trailing icon is clicked.
            DropdownMenu(
                expanded = expanded, // Visibility controlled by the expanded state.
                onDismissRequest = {
                    expanded = false
                    Log.d(TAG, "Dropdown dismissed")
                }, // Closes the menu when clicked outside.
                modifier = Modifier.fillMaxWidth() // Matches the width of the text field.
            ) {
                // List of priority options to display in the dropdown.
                listOf("All", "High", "Medium", "Low").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) }, // Displays the option text.
                        onClick = {
                            // Updates the selected priority, closes the menu, and notifies the ViewModel and caller.
                            selectedPriority = option
                            expanded = false
                            viewModel.setFilter(option) // Updates the filter in the ViewModel.
                            onFilterSelected(option) // Invokes the callback with the selected filter.
                            Log.d(TAG, "Filter selected: $option")
                        }
                    )
                }
            }
        }
    }
}
