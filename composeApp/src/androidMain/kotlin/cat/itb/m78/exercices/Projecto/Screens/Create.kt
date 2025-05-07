package cat.itb.m78.exercices.Projecto.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.itb.m78.exercices.Projecto.ViewModels.CreateViewModel
import cat.itb.m78.exercices.db.Database
import cat.itb.m78.exercices.Projecto.ComposeFileProvider
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class) // necesario para usar el topbar
@Composable
fun CreateScreen(
    database: Database,// la base de datos
    lat: Float,
    long: Float,
    onSaveComplete: () -> Unit
) {
    val viewModel = viewModel { CreateViewModel(database) } // instanciamos el viewModel
    // no es exactamente como lo haciamos antes pero sigues sin usar logica en la pnatala asi que creo que no hay problema con ello

    val context = LocalContext.current // importamos el context

    // Lanzador para seleccionar imagen de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.imageUri = it }
    }

    // Lanzador para tomar foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Forzar una actualización de estado para que Compose recomponga
            val currentUri = viewModel.imageUri
            viewModel.imageUri = null  // Resetea temporalmente
            viewModel.imageUri = currentUri  // Reasigna para forzar recomposición
        }
    }

    Scaffold( // scfold necesario para isar topbar
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Restaurante") },
                navigationIcon = {
                    IconButton(onClick = onSaveComplete) { // para tirar atras y no crear nada
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        }
    ) { padding -> // evitamos que se pongan encima unos de otros
        Column( // contenedor principal
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp), // doble padding para pillar buen margen, interesante uso
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Coordenadas (no editables)
            Text(
                "Coordenadas: $lat, $long",
                style = MaterialTheme.typography.titleMedium
            )

            // Título
            OutlinedTextField(
                value = viewModel.titulo, // valor actual, en este caso ninguno
                onValueChange = { viewModel.titulo = it }, // para que cuando cambie se envie al viewmodel
                label = { Text("Nombre del restaurante") },
                modifier = Modifier.fillMaxWidth() // que ocupe lo maximo
            )

            // Descripción
            OutlinedTextField(
                value = viewModel.descripcion,
                onValueChange = { viewModel.descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3 // para que el campo guarde texto mas largo
            )

            // Precio
            OutlinedTextField(
                value = viewModel.price,
                onValueChange = { viewModel.price = it },
                label = { Text("Precio medio €") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // contenedor de los botones de opciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { galleryLauncher.launch("image/*") }, // lanzador de galeria pero solo para imagenes, asi evitamos videos
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galería")
                }

                Button(
                    onClick = {
                        // Crear un archivo temporal para la foto
                        val uri = ComposeFileProvider.getImageUri(context)
                        cameraLauncher.launch(uri) // cuando tome la foto lo meta en el archivo temporal
                        viewModel.imageUri = uri // asignamos la imagen al campo en el viewmodel

                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cámara")
                }
            }

            // Vista previa de la imagen
            viewModel.imageUri?.let { uri -> // si hay uri imprime imagen
                AsyncImage(
                    model = uri, // pillamos la imagen del viewmodel
                    contentDescription = "Vista previa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)), // lados redondos
                    contentScale = ContentScale.Crop // ajusta la imagen rellenando el espacio y recortando si es necesario
                )
            }// Vista previa de la imagen



           // Spacer(modifier = Modifier.weight(1f)) // espacio para el boton

            // Botón guardar
            Button(
                onClick = { viewModel.saveRestaurant(lat, long, onSaveComplete) }, // llamamos a la funcion del viewmodel para guardar en la base de datos
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("GUARDAR")
            }
        }
    }
}