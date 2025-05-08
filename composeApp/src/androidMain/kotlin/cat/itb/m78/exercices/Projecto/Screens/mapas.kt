package cat.itb.m78.exercices.Projecto.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.itb.m78.exercices.Projecto.ViewModels.MapViewModel
import cat.itb.m78.exercices.db.Database
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    database: Database,
    onAddRestaurant: (Float, Float) -> Unit,
    onNavigateToList: () -> Unit
) {
    val viewModel = viewModel { MapViewModel(database) }
    val restaurants by viewModel.restaurants.collectAsState() // agarramos la lista y la preparamos para compose

    // Definir estado de cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(40.416775, -3.70379), // Centro de España por defecto
            6f // indiacamos zoom de camara al iniciarse
        )
    }

    Scaffold( // lo usamos para poder usar los botones en el mapa los Fabs
        floatingActionButton = { // este es un fab
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .offset(y = (-100).dp) // evitar que tape los botones de zoom del default de googlemaps

            ) {
                FloatingActionButton(
                    onClick = {
                        val target = cameraPositionState.position.target // pillamos la lozalizacion del centro de la camara
                        // pilla las posiciones pero espoco preciso, si tuviera mas tiempo lo cambiaria por algo de pulsar

                        onAddRestaurant(target.latitude.toFloat(), target.longitude.toFloat()) // nos vamos a la screen create
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir restaurante"
                    )
                }

                FloatingActionButton(onClick = onNavigateToList) { // nos vamos a lista
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Ver lista"
                    )
                }
                FloatingActionButton(onClick = { viewModel.loadRestaurants() }) { // actualizamos para que se vean las nuevas marcas
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar"
                    )
                }
            }
        }
    ) { padding ->
        GoogleMap( // creamos el google maps
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState, // la posicion inicial
            properties = MapProperties(
                isMyLocationEnabled = true, // poder ver la localizacion
                mapType = MapType.NORMAL // tipo de mapa, default de maps
            )
        ) {
            Marker(
                state = remember { MarkerState(
                    position = LatLng(41.45328178497256, 2.186292533727111) // como lo guarde con long pues se pone ne double
                ) },
                title = "ITB", // titulo
                snippet = "Precio medio: 500" // texto secundario para dar informacion util, como son restaurantes el precio
            )

            // Añadir marcadores para cada restaurante, con la variable que antes hemos seteado pues creamos los marcadores
            restaurants.forEach { restaurant ->
                Marker(
                    state = MarkerState(
                        position = LatLng(restaurant.lat.toDouble(), restaurant.long.toDouble()) // como lo guarde con long pues se pone ne double
                    ),
                    title = restaurant.titulo, // titulo
                    snippet = "Precio medio: ${restaurant.price}€" // texto secundario para dar informacion util, como son restaurantes el precio
                )
            }
        }
    }
}