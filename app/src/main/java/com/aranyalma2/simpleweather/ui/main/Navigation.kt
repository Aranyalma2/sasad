package com.aranyalma2.simpleweather.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aranyalma2.simpleweather.ui.forecast.ForecastScreen
import com.aranyalma2.simpleweather.ui.home.HomeScreen
import com.aranyalma2.simpleweather.ui.search.SearchScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Forecast : Screen("forecast/{locationId}") {
        fun createRoute(locationId: Int) = "forecast/$locationId"
    }
}

@Composable
fun WeatherNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToForecast = { locationId ->
                    navController.navigate(Screen.Forecast.createRoute(locationId))
                }
            )
        }

                composable(Screen.Search.route) {
                    SearchScreen(
                        navigateBack = { navController.popBackStack() },

                    )
                }
        /*
                        composable(
                            route = Screen.Forecast.route,
                            arguments = listOf(navArgument("locationId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val locationId = backStackEntry.arguments?.getInt("locationId") ?: 0
                            ForecastScreen(
                                locationId = locationId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                         */
    }
}