package com.gpsdavida.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gpsdavida.app.R
import com.gpsdavida.app.ui.agora.AgoraScreen
import com.gpsdavida.app.ui.events.EventFormScreen
import com.gpsdavida.app.ui.events.EventsListScreen
import com.gpsdavida.app.ui.habits.HabitFormScreen
import com.gpsdavida.app.ui.habits.HabitsListScreen
import com.gpsdavida.app.ui.meudia.MeuDiaScreen
import com.gpsdavida.app.ui.tasks.TaskFormScreen
import com.gpsdavida.app.ui.tasks.TasksListScreen

@Composable
fun GpsNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = currentRoute in setOf(
        GpsRoutes.AGORA,
        GpsRoutes.MEU_DIA,
        GpsRoutes.EVENTS,
        GpsRoutes.TASKS,
        GpsRoutes.HABITS,
    )

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == GpsRoutes.AGORA,
                        onClick = { navController.navigateToTab(GpsRoutes.AGORA) },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_agora)) },
                    )
                    NavigationBarItem(
                        selected = currentRoute == GpsRoutes.MEU_DIA,
                        onClick = { navController.navigateToTab(GpsRoutes.MEU_DIA) },
                        icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_meu_dia)) },
                    )
                    NavigationBarItem(
                        selected = currentRoute == GpsRoutes.EVENTS,
                        onClick = { navController.navigateToTab(GpsRoutes.EVENTS) },
                        icon = { Icon(Icons.Filled.List, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_eventos)) },
                    )
                    NavigationBarItem(
                        selected = currentRoute == GpsRoutes.TASKS,
                        onClick = { navController.navigateToTab(GpsRoutes.TASKS) },
                        icon = { Icon(Icons.Filled.Check, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_tarefas)) },
                    )
                    NavigationBarItem(
                        selected = currentRoute == GpsRoutes.HABITS,
                        onClick = { navController.navigateToTab(GpsRoutes.HABITS) },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_habitos)) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = GpsRoutes.AGORA,
            modifier = Modifier.padding(padding),
        ) {
            composable(GpsRoutes.AGORA) { AgoraScreen() }
            composable(GpsRoutes.MEU_DIA) {
                MeuDiaScreen(
                    onAddEvent = { navController.navigate(GpsRoutes.eventEditor()) },
                    onOpenEvent = { id -> navController.navigate(GpsRoutes.eventEditor(id)) },
                    onOpenTask = { id -> navController.navigate(GpsRoutes.taskEditor(id)) },
                    onOpenHabit = { id -> navController.navigate(GpsRoutes.habitEditor(id)) },
                )
            }
            composable(GpsRoutes.EVENTS) {
                EventsListScreen(
                    onAdd = { navController.navigate(GpsRoutes.eventEditor()) },
                    onOpen = { id -> navController.navigate(GpsRoutes.eventEditor(id)) },
                )
            }
            composable(GpsRoutes.TASKS) {
                TasksListScreen(
                    onAdd = { navController.navigate(GpsRoutes.taskEditor()) },
                    onOpen = { id -> navController.navigate(GpsRoutes.taskEditor(id)) },
                )
            }
            composable(GpsRoutes.HABITS) {
                HabitsListScreen(
                    onAdd = { navController.navigate(GpsRoutes.habitEditor()) },
                    onOpen = { id -> navController.navigate(GpsRoutes.habitEditor(id)) },
                )
            }
            composable(
                route = GpsRoutes.EVENT_EDITOR,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
            ) {
                EventFormScreen(onDone = { navController.popBackStack() })
            }
            composable(
                route = GpsRoutes.TASK_EDITOR,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType }),
            ) {
                TaskFormScreen(onDone = { navController.popBackStack() })
            }
            composable(
                route = GpsRoutes.HABIT_EDITOR,
                arguments = listOf(navArgument("habitId") { type = NavType.StringType }),
            ) {
                HabitFormScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
