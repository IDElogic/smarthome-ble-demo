package com.iwoioapps.smarthomebledemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iwoioapps.smarthomebledemo.ui.scan.ScanScreen
import com.iwoioapps.smarthomebledemo.ui.smarthome.SmartHomeScreen

private const val ADDRESS_ARG = "address"

sealed class Destination(val route: String) {
    data object Scan : Destination("scan")
    data object SmartHome : Destination("smart_home/{$ADDRESS_ARG}") {
        fun createRoute(address: String) = "smart_home/$address"
    }
}

@Composable
fun BleDemoNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Destination.Scan.route) {
        composable(Destination.Scan.route) {
            ScanScreen(
                onDeviceSelected = { address ->
                    navController.navigate(Destination.SmartHome.createRoute(address))
                }
            )
        }
        composable(
            route = Destination.SmartHome.route,
            arguments = listOf(navArgument(ADDRESS_ARG) { type = NavType.StringType })
        ) {
            SmartHomeScreen(onBack = { navController.popBackStack() })
        }
    }
}
