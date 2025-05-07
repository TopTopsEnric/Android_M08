package cat.itb.m78.exercices.Projecto.Screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.itb.m78.exercices.Projecto.ViewModels.MapViewModel
import cat.itb.m78.exercices.db.Database
import coil.compose.AsyncImage
import databases.MyMarcador

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantListScreen(
    database: Database,
    onEdit: (Long) -> Unit,
    onBackToMap: () -> Unit
) {
    val viewModel = viewModel { MapViewModel(database) }
    val restaurants by viewModel.restaurants.collectAsState()

    // Cada vez que se muestra la pantalla, actualizamos la lista
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurantes de Ramen") },
                navigationIcon = {
                    IconButton(onClick = onBackToMap) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver al mapa")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(restaurants) { restaurant -> // se encarga de crear un elemento de lista por cada restaurante
                RestaurantItem(
                    restaurant = restaurant,
                    onEditClick = { onEdit(restaurant.id) }
                )
            }
        }
    }
}

@Composable
fun RestaurantItem(
    restaurant: MyMarcador,
    onEditClick: () -> Unit
) {
    Card( // contenedor para darle efecto
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick), // hace que se pueda pulsar
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del restaurante
            AsyncImage(
                model = Uri.parse(restaurant.image), // tranforma el string en Uri y la pasa como imagen
                contentDescription = restaurant.titulo,
                modifier = Modifier
                    .size(80.dp) // tamaño
                    .clip(CircleShape),
                contentScale = ContentScale.Crop // recorta si es necesario
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.titulo,
                    style = MaterialTheme.typography.titleLarge  // En lugar de h6
                )
                Text(
                    text = restaurant.descripcio,
                    style = MaterialTheme.typography.bodyMedium,  // En lugar de body2
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Precio medio: ${restaurant.price}€",
                    style = MaterialTheme.typography.bodySmall  // En lugar de caption
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }
    }
}