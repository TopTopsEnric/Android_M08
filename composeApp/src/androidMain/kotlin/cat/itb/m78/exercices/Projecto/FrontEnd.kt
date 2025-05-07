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
    permissions: List<String>, // lista de permisos
    onAllGranted: @Composable () -> Unit //  entre los parentesis indicas el composable que llamara si estan todos
) {
    val permState = rememberMultiplePermissionsState(permissions) // "variable fija" que se usa para acordarte de que te los han concedido

    // Se usa para llamar a la funcion Unit la primera vez que se genera
    LaunchedEffect(Unit) {
        if (!permState.allPermissionsGranted) { // si no estan todos los permisos concedidos, recuerda que se puede seetar fuera de la app
            permState.launchMultiplePermissionRequest()// lanza las ventanas esas de permisos
        }
    }

    when {
        // Cuando estan todos concedidos es cuando se ejecuta el onAllGranted i nos permite ir a composable pertinente
        permState.allPermissionsGranted -> {
            onAllGranted()
        }
        // este rationale muestra la "pantalla" cuando determina que el usuario necesita una explicacion, por si falla algo al pedirlos
        // en nuestro contexto es cuando rechaza los permisos muestra la pantalla para volver a pedirlos
        permState.shouldShowRationale -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // que quede centrado
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Necesitamos permisos para continuar.") // informamos que no pasa sin los permisos
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        // volvemos a llamar a los permisos si le da al boton
                        permState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Conceder permisos")
                }
            }
        }
        // Por si los rechaza explicitamente le recordamos que tiene que pedirlos
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
                        // llamamos lo de los permisos
                        permState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Pedir permisos")
                }
            }
        }
    }
}

// Controlador  de toda la app, pasamos la database para no tener que llamarla en cada escena, muy practico.
@Composable
fun RamenApp(database: Database) {
    val navController = rememberNavController() // creamos el navController

    // contenedor principal, seteamos que la pantalla incial es map
    NavHost(navController = navController, startDestination = "map") {

        // 1) MAP SCREEN: necesita permisos de localización (fine + coarse)
        composable("map") {
            PermissionRequired(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION, // pedimos los permisos, los permisos deben estar en el manifiesto de android
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                MapScreen(
                    database = database,
                    onAddRestaurant = { lat, long ->
                        navController.navigate("create/$lat/$long") // funciones de navegacion con sus respectivos parametros
                    },
                    onNavigateToList = {
                        navController.navigate("list") // lo mismo pero sin parametros
                    }
                )
            }
        }

        // 2) CREATE SCREEN: requiere permisos de cámara (para TakePicture)
        composable(
            "create/{lat}/{long}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("long") { type = NavType.FloatType } // tipas y guardas en lista
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments!!.getFloat("lat") // agarras los valores de la lista y los seteas
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
                    onSaveComplete = { navController.popBackStack() } // callback para moviles, va a la pantalla anterior
                )
            }
        }

        // 3) LIST SCREEN: no necesita permisos especiales
        composable("list") {
            RestaurantListScreen(
                database = database,
                onEdit = { id -> navController.navigate("edit/$id") }, // para ir la pantalla edit
                onBackToMap = { navController.popBackStack() } // callback para moviles
            )
        }

        // 4) EDIT SCREEN: igual que Create, necesita cámara y escritura
        composable(
            "edit/{restaurantId}",
            arguments = listOf(navArgument("restaurantId") { type = NavType.LongType }) // definimos ruta i tipamos
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments!!.getLong("restaurantId")

            PermissionRequired(
                permissions = listOf(
                    Manifest.permission.CAMERA, // permiso de camera

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
