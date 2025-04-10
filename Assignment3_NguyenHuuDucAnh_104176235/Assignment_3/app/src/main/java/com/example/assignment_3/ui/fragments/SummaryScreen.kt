package com.example.assignment_3.ui.fragments

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import com.example.assignment_3.R
import com.example.assignment_3.ui.viewmodel.TaskViewModel
import kotlin.math.min
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import java.text.DecimalFormat  // Added for formatting percentage

private const val TAG = "SummaryScreen"  // Added TAG constant for logging

// SummaryScreen is a composable function that displays a summary of task statistics,
// including total tasks, completion rates, and priority breakdowns, using cards and circular indicators.
// It takes a TaskViewModel to fetch and observe task data.
@Composable
fun SummaryScreen(viewModel: TaskViewModel) {
    // Load custom colors from resources for consistent theming across the UI.
    // These colors are used for visual indicators like charts and legends.
    val darkRed = colorResource(id = R.color.dark_red) // Color for high priority or incomplete tasks.
    val darkGreen = colorResource(id = R.color.dark_green) // Color for completed or low priority tasks.
    val orange = colorResource(id = R.color.orange) // Color for medium priority tasks.
    val darkMagenta = colorResource(id = R.color.dark_magenta) // Color for completion rate in the legend.

    // Observe LiveData from the ViewModel to get real-time updates on task statistics.
    // Each value is initialized to 0 to avoid null issues during initial composition.
    val totalTasks by viewModel.getTotalTasks().observeAsState(initial = 0) // Total number of tasks.
    val completedTasks by viewModel.getCompletedTasks().observeAsState(initial = 0) // Number of completed tasks.
    val completedTasksToday by viewModel.getCompletedTasksToday().observeAsState(initial = 0) // Tasks completed today.
    val tasksDueThisWeek by viewModel.getTasksDueThisWeek().observeAsState(initial = 0) // Tasks due within the current week.
    val highPriorityTasks by viewModel.getHighPriorityTasks().observeAsState(initial = 0) // Number of high priority tasks.
    val mediumPriorityTasks by viewModel.getMediumPriorityTasks().observeAsState(initial = 0) // Number of medium priority tasks.
    val lowPriorityTasks by viewModel.getLowPriorityTasks().observeAsState(initial = 0) // Number of low priority tasks.

    // Calculate derived values for display.
    val incompleteTasks = totalTasks - completedTasks // Number of incomplete tasks.
    // Calculate the completion rate as a float to retain decimal precision, avoiding division by zero.
    val completionRate = if (totalTasks > 0) (completedTasks.toFloat() * 100 / totalTasks) else 0f
    // Format the completion rate to one decimal place (e.g., "28.5%").
    val formattedCompletionRate = DecimalFormat("0.#").format(completionRate) + "%"

    // Get the current device configuration to determine the screen orientation.
    val configuration = LocalConfiguration.current
    // Check if the device is in landscape mode to adjust the layout accordingly.
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Dynamically calculate the width of statistic cards based on screen width and orientation.
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp // Get the screen width in dp.
    val cardWidth = if (isLandscape) {
        // In landscape mode, display 4 cards per row.
        // Subtract 32dp (16dp padding on each side) and 48dp (16dp spacing between 4 cards) from screen width.
        (screenWidth - 80.dp) / 4
    } else {
        // In portrait mode, display 2 cards per row.
        // Subtract 32dp (16dp padding on each side) and 16dp (spacing between 2 cards) from screen width.
        (screenWidth - 48.dp) / 2
    }

    // Log initial state and calculated values
    Log.d(TAG, "Screen initialized, isLandscape: $isLandscape, totalTasks: $totalTasks, " +
            "completedTasks: $completedTasks, completionRate: $formattedCompletionRate, " +
            "highPriority: $highPriorityTasks, mediumPriority: $mediumPriorityTasks, lowPriority: $lowPriorityTasks")

    // Use LazyColumn for efficient scrolling of the summary content.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // Fills the entire available space.
            .padding(16.dp), // Adds 16dp padding around the content for spacing.
        verticalArrangement = Arrangement.spacedBy(16.dp) // Adds 16dp vertical spacing between items.
    ) {
        // Section 1: Title
        item {
            // Display the screen title with a predefined typography style.
            Text(
                text = "Task Summary",
                style = MaterialTheme.typography.headlineSmall // Uses a small headline style for the title.
            )
        }

        // Section 2: Statistics Cards
        item {
            if (isLandscape) {
                // In landscape mode, display all 4 statistic cards in a single row.
                Row(
                    modifier = Modifier.fillMaxWidth(), // Fills the available width.
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Adds 16dp spacing between cards.
                ) {
                    // Display cards for total tasks, completion rate, completed today, and tasks due this week.
                    StatCard("Total Tasks", totalTasks.toString(), cardWidth)
                    StatCard("Completion Rate", formattedCompletionRate, cardWidth)
                    StatCard("Completed Today", completedTasksToday.toString(), cardWidth)
                    StatCard("This Week", tasksDueThisWeek.toString(), cardWidth)
                }
            } else {
                // In portrait mode, display statistic cards in two rows with 2 cards each.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard("Total Tasks", totalTasks.toString(), cardWidth)
                    StatCard("Completion Rate", formattedCompletionRate, cardWidth)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard("Completed Today", completedTasksToday.toString(), cardWidth)
                    StatCard("This Week", tasksDueThisWeek.toString(), cardWidth)
                }
            }
        }

        // Section 3: Task Completion and Priority Breakdown Visualizations
        if (isLandscape) {
            item {
                // In landscape mode, display Task Completion and Priority Breakdown side by side.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly // Distributes space evenly between the two sections.
                ) {
                    // Task Completion Section
                    Column(
                        modifier = Modifier
                            .weight(1f), // Takes equal space in the row.
                        horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally.
                    ) {
                        Text(
                            text = "Task Completion",
                            style = MaterialTheme.typography.titleMedium // Medium title style for the section.
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Adds 8dp vertical spacing.
                        // Display a circular indicator for task completion status.
                        CircularTaskIndicator(
                            completedTasks = completedTasks,
                            incompleteTasks = incompleteTasks,
                            completedColor = darkGreen,
                            incompleteColor = darkRed,
                            modifier = Modifier.size(150.dp) // Sets the size of the circular indicator.
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Display a legend for the task completion indicator.
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp), // Adds horizontal padding to the legend.
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LegendItem(color = darkGreen, label = "Completed", value = completedTasks)
                            LegendItem(color = darkRed, label = "Incomplete", value = incompleteTasks)
                            LegendItem(color = darkMagenta, label = "Rate", value = formattedCompletionRate)
                        }
                    }

                    // Priority Breakdown Section
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Priority Breakdown",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Display a circular indicator for task priority distribution.
                        CircularPriorityIndicator(
                            highPriority = highPriorityTasks,
                            mediumPriority = mediumPriorityTasks,
                            lowPriority = lowPriorityTasks,
                            highColor = darkRed,
                            mediumColor = orange,
                            lowColor = darkGreen,
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Display a legend for the priority breakdown indicator.
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LegendItem(color = darkRed, label = "High", value = highPriorityTasks)
                            LegendItem(color = orange, label = "Medium", value = mediumPriorityTasks)
                            LegendItem(color = darkGreen, label = "Low", value = lowPriorityTasks)
                        }
                    }
                }
            }
        } else {
            // In portrait mode, display Task Completion and Priority Breakdown sections vertically.
            item {
                // Task Completion Section
                Text(
                    text = "Task Completion",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically // Aligns the indicator and legend vertically.
                ) {
                    CircularTaskIndicator(
                        completedTasks = completedTasks,
                        incompleteTasks = incompleteTasks,
                        completedColor = darkGreen,
                        incompleteColor = darkRed,
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp)) // Adds 16dp horizontal spacing.
                    // Display the legend next to the indicator.
                    Column {
                        LegendItem(color = darkGreen, label = "Completed Tasks", value = completedTasks)
                        LegendItem(color = darkRed, label = "Incomplete Tasks", value = incompleteTasks)
                        LegendItem(color = darkMagenta, label = "Completion Rate", value = formattedCompletionRate)
                    }
                }
            }

            item {
                // Priority Breakdown Section
                Text(
                    text = "Priority Breakdown",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularPriorityIndicator(
                        highPriority = highPriorityTasks,
                        mediumPriority = mediumPriorityTasks,
                        lowPriority = lowPriorityTasks,
                        highColor = darkRed,
                        mediumColor = orange,
                        lowColor = darkGreen,
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Display the legend next to the indicator.
                    Column {
                        LegendItem(color = darkRed, label = "High Priority", value = highPriorityTasks)
                        LegendItem(color = orange, label = "Medium Priority", value = mediumPriorityTasks)
                        LegendItem(color = darkGreen, label = "Low Priority", value = lowPriorityTasks)
                    }
                }
            }
        }
    }
}

// StatCard is a reusable composable that displays a single statistic in a card format.
// It takes a title, value, and card width as parameters.
@Composable
fun StatCard(title: String, value: String, cardWidth: androidx.compose.ui.unit.Dp) {
    Log.d(TAG, "Rendering StatCard: $title = $value")
    Card(
        modifier = Modifier
            .width(cardWidth) // Sets the card width to the calculated value.
            .padding(8.dp), // Adds 8dp padding around the card for spacing.
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Adds a 4dp elevation for a shadow effect.
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // Adds 16dp padding inside the card.
            horizontalAlignment = Alignment.CenterHorizontally // Centers the content horizontally.
        ) {
            // Display the title of the statistic.
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            // Display the value of the statistic with a larger font.
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

// LegendItem is a reusable composable that displays a single legend entry with a colored circle and text.
// It takes a color, label, and value as parameters.
@Composable
fun LegendItem(color: Color, label: String, value: Any) {
    Log.d(TAG, "Rendering LegendItem: $label = $value")
    Row(
        verticalAlignment = Alignment.CenterVertically, // Aligns the circle and text vertically.
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp) // Adds 2dp vertical padding for spacing between legend items.
    ) {
        // Draw a small colored circle to represent the data category.
        Canvas(modifier = Modifier.size(16.dp)) {
            drawCircle(color = color, radius = size.width / 2) // Draws a circle with the specified color.
        }
        Spacer(modifier = Modifier.width(8.dp)) // Adds 8dp spacing between the circle and text.
        // Display the label and value with ellipsis if the text is too long.
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f), // Takes remaining space in the row.
            maxLines = 1, // Limits the text to a single line.
            overflow = TextOverflow.Ellipsis // Adds ellipsis if the text overflows.
        )
    }
}

// CircularTaskIndicator is a composable that displays a circular chart showing the proportion of completed vs. incomplete tasks.
// It takes the number of completed and incomplete tasks, their respective colors, and a modifier for customization.
@Composable
fun CircularTaskIndicator(
    completedTasks: Int,
    incompleteTasks: Int,
    completedColor: Color,
    incompleteColor: Color,
    modifier: Modifier = Modifier
) {
    // Calculate the total number of tasks for proportion calculations.
    val total = (completedTasks + incompleteTasks).toFloat()
    // Calculate the sweep angle for completed tasks (in degrees), avoiding division by zero.
    val completedSweepAngle = if (total > 0) (completedTasks / total) * 360f else 0f
    // Calculate the completion rate as a float for display in the center, formatted to one decimal place.
    val completionRate = if (total > 0) (completedTasks.toFloat() * 100 / total) else 0f
    val formattedCompletionRate = DecimalFormat("0.#").format(completionRate) + "%"

    Log.d(TAG, "Rendering CircularTaskIndicator: completedTasks: $completedTasks, " +
            "incompleteTasks: $incompleteTasks, completionRate: $formattedCompletionRate, " +
            "sweepAngle: $completedSweepAngle")

    // Use a Box to overlay the completion rate text on the circular chart.
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center // Centers the text within the chart.
    ) {
        // Draw the circular chart using Canvas.
        Canvas(modifier = Modifier.matchParentSize()) { // Matches the size of the parent Box.
            val canvasWidth = size.width // Get the width of the canvas.
            val canvasHeight = size.height // Get the height of the canvas.
            val radius = min(canvasWidth, canvasHeight) / 2f // Calculate the radius as half of the smaller dimension.
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f) // Calculate the center point of the canvas.

            // Draw the background circle representing incomplete tasks (full circle).
            drawArc(
                color = incompleteColor,
                startAngle = 0f, // Starts at 0 degrees.
                sweepAngle = 360f, // Draws a full circle.
                useCenter = true, // Fills the arc to the center, creating a pie chart effect.
                topLeft = Offset(center.x - radius, center.y - radius), // Position of the arc.
                size = Size(radius * 2, radius * 2) // Size of the arc.
            )

            // Draw the foreground arc representing completed tasks.
            drawArc(
                color = completedColor,
                startAngle = -90f, // Starts at the top (90 degrees counterclockwise from 0).
                sweepAngle = completedSweepAngle, // Draws the proportion of completed tasks.
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
        // Display the completion rate in the center of the chart.
        Text(
            text = formattedCompletionRate,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground // Uses the theme's onBackground color for contrast.
        )
    }
}

// CircularPriorityIndicator is a composable that displays a circular chart showing the distribution of tasks by priority.
// It takes the number of high, medium, and low priority tasks, their respective colors, and a modifier for customization.
@Composable
fun CircularPriorityIndicator(
    highPriority: Int,
    mediumPriority: Int,
    lowPriority: Int,
    highColor: Color,
    mediumColor: Color,
    lowColor: Color,
    modifier: Modifier = Modifier
) {
    // Calculate the total number of tasks for proportion calculations.
    val total = (highPriority + mediumPriority + lowPriority).toFloat()
    // Calculate the sweep angles for each priority level (in degrees), avoiding division by zero.
    val highSweepAngle = if (total > 0) (highPriority / total) * 360f else 0f
    val mediumSweepAngle = if (total > 0) (mediumPriority / total) * 360f else 0f
    val lowSweepAngle = if (total > 0) (lowPriority / total) * 360f else 0f

    Log.d(TAG, "Rendering CircularPriorityIndicator: highPriority: $highPriority, " +
            "mediumPriority: $mediumPriority, lowPriority: $lowPriority, " +
            "highSweepAngle: $highSweepAngle, mediumSweepAngle: $mediumSweepAngle, " +
            "lowSweepAngle: $lowSweepAngle")

    // Draw the circular chart using Canvas.
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = min(canvasWidth, canvasHeight) / 2f
        val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

        // Draw the high priority segment.
        drawArc(
            color = highColor,
            startAngle = -90f, // Starts at the top.
            sweepAngle = highSweepAngle, // Draws the proportion of high priority tasks.
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // Draw the medium priority segment, starting where the high priority segment ends.
        drawArc(
            color = mediumColor,
            startAngle = -90f + highSweepAngle, // Continues from the end of the high priority segment.
            sweepAngle = mediumSweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // Draw the low priority segment, starting where the medium priority segment ends.
        drawArc(
            color = lowColor,
            startAngle = -90f + highSweepAngle + mediumSweepAngle, // Continues from the end of the medium priority segment.
            sweepAngle = lowSweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
    }
}
