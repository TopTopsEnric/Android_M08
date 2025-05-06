package cat.itb.m78.exercices.Projecto

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cat.itb.m78.exercices.Projecto.Screens.CreateScreen
import cat.itb.m78.exercices.Projecto.Screens.MapScreen
import cat.itb.m78.exercices.Projecto.Screens.EditScreen
import cat.itb.m78.exercices.Projecto.Screens.RestaurantListScreen
import cat.itb.m78.exercices.db.Database
import com.google.accompanist.permissions.*

/**
 * Wrapper genérico que solicita una lista de permisos y
 * solo cuando todos están invoca onAllGranted.
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequired(
    permissions: List<String>,
    onAllGranted: @Composable () -> Unit
) {
    val permState = rememberMultiplePermissionsState(permissions)

    // ✅ Petición inicial en un side‐effect
    LaunchedEffect(Unit) {
        if (!permState.allPermissionsGranted) {
            permState.launchMultiplePermissionRequest()
        }
    }

    when {
        // TODOS los permisos concedidos
        permState.allPermissionsGranted -> {
            onAllGranted()
        }
        // Mostrar rationale si ya denegó al menos una vez
        permState.shouldShowRationale -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Necesitamos permisos para continuar.")
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        // ✅ Solo aquí, dentro del onClick
                        permState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Conceder permisos")
                }
            }
        }
        // Primera vez o denegado sin rationale
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Para usar esta funcionalidad necesitamos permisos.")
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        // ✅ Y también aquí, dentro del onClick
                        permState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Pedir permisos")
                }
            }
        }
    }
}

@Composable
fun RamenApp(database: Database) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "map") {

        // 1) MAP SCREEN: necesita permisos de localización (fine + coarse)
        composable("map") {
            PermissionRequired(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
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
        }

        // 2) CREATE SCREEN: requiere permisos de cámara y escritura (para TakePicture)
        composable(
            "create/{lat}/{long}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("long") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments!!.getFloat("lat")
            val long = backStackEntry.arguments!!.getFloat("long")

            PermissionRequired(
                permissions = listOf(
                    Manifest.permission.CAMERA,

                )
            ) {
                CreateScreen(
                    database = database,
                    lat = lat,
                    long = long,
                    onSaveComplete = { navController.popBackStack() }
                )
            }
        }

        // 3) LIST SCREEN: no necesita permisos especiales
        composable("list") {
            RestaurantListScreen(
                database = database,
                onEdit = { id -> navController.navigate("edit/$id") },
                onBackToMap = { navController.popBackStack() }
            )
        }

        // 4) EDIT SCREEN: igual que Create, necesita cámara y escritura
        composable(
            "edit/{restaurantId}",
            arguments = listOf(navArgument("restaurantId") { type = NavType.LongType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments!!.getLong("restaurantId")

            PermissionRequired(
                permissions = listOf(
                    Manifest.permission.CAMERA,

                )
            ) {
                EditScreen(
                    database = database,
                    restaurantId = restaurantId,
                    onSaveComplete = { navController.popBackStack() }
                )
            }
        }
    }
}
