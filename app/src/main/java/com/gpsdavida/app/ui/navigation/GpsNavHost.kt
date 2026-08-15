package com.gpsdavida.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gpsdavida.app.R
import com.gpsdavida.app.ui.agora.AgoraScreen
import com.gpsdavida.app.ui.meudia.MeuDiaScreen

@Composable
fun GpsNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
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
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = GpsRoutes.AGORA,
            modifier = Modifier.padding(padding),
        ) {
            composable(GpsRoutes.AGORA) { AgoraScreen() }
            composable(GpsRoutes.MEU_DIA) { MeuDiaScreen() }
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
