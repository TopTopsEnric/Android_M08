package cat.itb.m78.exercices.Projecto.ViewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.itb.m78.exercices.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateViewModel(private val database: Database) : ViewModel() {

    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)
    var price by mutableStateOf("")

    fun saveRestaurant(lat: Float, long: Float, onComplete: () -> Unit) {
        if (titulo.isBlank() || descripcion.isBlank() || imageUri == null || price.isBlank()) {
            return // Validación fallida
        }

        viewModelScope.launch(Dispatchers.IO) {
            database.databaseQueries.InsertOne(
                titulo = titulo,
                descripcio = descripcion,
                lat = lat.toLong(),
                long = long.toLong(),
                image = imageUri.toString(),
                price = price.toIntOrNull()?.toLong() ?: 0L
            )

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}