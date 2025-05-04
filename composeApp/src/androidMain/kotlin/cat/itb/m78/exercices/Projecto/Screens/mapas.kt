package cat.itb.m78.exercices.Projecto.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
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
    val restaurants by viewModel.restaurants.collectAsState()

    // Definir estado de cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(40.416775, -3.70379), // Centro de España por defecto
            6f
        )
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        val target = cameraPositionState.position.target
                        onAddRestaurant(target.latitude.toFloat(), target.longitude.toFloat())
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir restaurante"
                    )
                }

                FloatingActionButton(onClick = onNavigateToList) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Ver lista"
                    )
                }
            }
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            )
        ) {
            // Añadir marcadores para cada restaurante
            restaurants.forEach { restaurant ->
                Marker(
                    state = MarkerState(
                        position = LatLng(restaurant.lat.toDouble(), restaurant.long.toDouble())
                    ),
                    title = restaurant.titulo,
                    snippet = "Precio medio: ${restaurant.price}€"
                )
            }
        }
    }
}