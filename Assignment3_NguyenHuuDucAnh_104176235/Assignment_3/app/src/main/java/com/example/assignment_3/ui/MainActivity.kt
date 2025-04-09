package com.example.assignment_3.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment_3.ui.fragments.TaskListScreen
import com.example.assignment_3.ui.fragments.SummaryScreen
import com.example.assignment_3.ui.fragments.FilterScreen
import com.example.assignment_3.ui.AddEditScreen
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import com.example.assignment_3.ui.viewmodel.TaskViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState

// MainActivity is the entry point of the app, extending ComponentActivity to use Jetpack Compose.
class MainActivity : ComponentActivity() {
    // onCreate is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content of the activity using Jetpack Compose.
        setContent {
            TaskMasterApp() // Launch the main composable for the app.
        }
    }
}

// TaskMasterApp is the root composable that sets up the app's navigation and UI structure.
// It uses a Scaffold with a bottom navigation bar and a NavHost for screen navigation.
@Composable
fun TaskMasterApp() {
    // Create a NavController to manage navigation between screens.
    val navController = rememberNavController()
    // Initialize the TaskViewModel using the viewModel() factory, shared across all screens.
    val viewModel: TaskViewModel = viewModel()

    // Use Scaffold to provide a consistent layout structure with a bottom navigation bar.
    Scaffold(
        bottomBar = {
            // Display the bottom navigation bar, passing the NavController to handle navigation.
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        // NavHost manages the navigation graph and renders the appropriate screen based on the current route.
        NavHost(
            navController = navController,
            startDestination = "taskList", // Set the starting screen to the task list.
            modifier = Modifier.padding(innerPadding) // Apply padding to account for the bottom bar.
        ) {
            // Define the "taskList" route, which displays the TaskListScreen.
            composable("taskList") {
                TaskListScreen(
                    viewModel = viewModel, // Pass the shared ViewModel to manage task data.
                    onTaskClick = { task ->
                        // Navigate to the AddEditScreen with the task's ID when a task is clicked.
                        navController.navigate("addEdit/${task.id}")
                    },
                    onAddTask = {
                        // Navigate to the AddEditScreen with taskId -1 to add a new task.
                        navController.navigate("addEdit/-1")
                    }
                )
            }
            // Define the "summary" route, which displays the SummaryScreen.
            composable("summary") {
                SummaryScreen(viewModel = viewModel) // Pass the shared ViewModel to display task statistics.
            }
            // Define the "filter" route, which displays the FilterScreen.
            composable("filter") {
                FilterScreen(
                    viewModel = viewModel, // Pass the shared ViewModel to manage filter state.
                    onFilterSelected = { priority ->
                        // Navigate back to the task list after a filter is selected.
                        navController.navigate("taskList")
                    }
                )
            }
            // Define the "addEdit/{taskId}" route with a dynamic taskId parameter.
            composable(
                "addEdit/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.IntType }) // Define taskId as an integer argument.
            ) { backStackEntry ->
                // Extract the taskId from the navigation arguments, defaulting to -1 if not found.
                val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
                AddEditScreen(
                    taskId = taskId, // Pass the taskId to determine if we're adding or editing a task.
                    viewModel = viewModel, // Pass the shared ViewModel to manage task data.
                    navController = navController // Pass the NavController to handle navigation.
                )
            }
        }
    }
}

// BottomNavigationBar is a composable that displays a bottom navigation bar with items for navigating between screens.
// It takes a NavHostController to handle navigation events.
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Define the list of navigation items, each with a title, route, and icon.
    val items = listOf(
        BottomNavItem("Tasks", "taskList", Icons.Default.List), // Item for the task list screen.
        BottomNavItem("Summary", "summary", Icons.Default.Info), // Item for the summary screen.
        BottomNavItem("Filter", "filter", Icons.Default.Filter) // Item for the filter screen.
    )
    // Observe the current route to determine which navigation item is selected.
    val currentRoute by navController.currentBackStackEntryAsState()
    val selectedRoute = currentRoute?.destination?.route

    // Use NavigationBar to display the bottom navigation items.
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) }, // Display the item's icon.
                label = { Text(item.title) }, // Display the item's title below the icon.
                selected = selectedRoute == item.route, // Highlight the item if its route matches the current route.
                onClick = {
                    if (selectedRoute != item.route) { // Only navigate if the current route is different.
                        navController.navigate(item.route) {
                            // Pop the back stack up to the start destination to avoid stacking screens.
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true // Ensure only one instance of the destination is on the stack.
                        }
                    }
                }
            )
        }
    }
}

// BottomNavItem is a data class that represents a single item in the bottom navigation bar.
// It holds the title, route, and icon for each navigation item.
data class BottomNavItem(val title: String, val route: String, val icon: ImageVector)