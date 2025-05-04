package cat.itb.m78.exercices.Projecto

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cat.itb.m78.exercices.Projecto.Screens.CreateScreen
import cat.itb.m78.exercices.Projecto.Screens.EditScreen
import cat.itb.m78.exercices.Projecto.Screens.MapScreen
import cat.itb.m78.exercices.Projecto.Screens.RestaurantListScreen
import cat.itb.m78.exercices.db.Database

class FrontEnd {
    @Composable
    fun RamenApp(database: Database) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "map") {
            composable("map") {
                MapScreen(
                    database = database,
                    onAddRestaurant = { lat, long ->
                        navController.navigate("create/$lat/$long")
                    },
                    onNavigateToList = {
                        navController.navigate("list")
                    }
                )
            }
            composable(
                "create/{lat}/{long}",
                arguments = listOf(
                    navArgument("lat") { type = NavType.FloatType },
                    navArgument("long") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getFloat("lat") ?: 0f
                val long = backStackEntry.arguments?.getFloat("long") ?: 0f

                CreateScreen(
                    database = database,
                    lat = lat,
                    long = long,
                    onSaveComplete = { navController.popBackStack() }
                )
            }
            composable("list") {
                RestaurantListScreen(
                    database = database,
                    onEdit = { restaurantId ->
                        navController.navigate("edit/$restaurantId")
                    },
                    onBackToMap = { navController.popBackStack() }
                )
            }
            composable(
                "edit/{restaurantId}",
                arguments = listOf(navArgument("restaurantId") { type = NavType.LongType })
            ) { backStackEntry ->
                val restaurantId = backStackEntry.arguments?.getLong("restaurantId") ?: -1L

                EditScreen(
                    database = database,
                    restaurantId = restaurantId,
                    onSaveComplete = { navController.popBackStack() }
                )
            }
        }
    }
}