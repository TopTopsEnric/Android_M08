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

class EditViewModel(private val database: Database) : ViewModel() {

    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)
    var price by mutableStateOf("")
    var lat by mutableStateOf(0L)
    var long by mutableStateOf(0L)

    private var restaurantId = -1L

    fun loadRestaurant(id: Long) {
        restaurantId = id
        viewModelScope.launch(Dispatchers.IO) {
            val restaurant = database.databaseQueries.SelectOne(id).executeAsOneOrNull() // pillamos el objeto
            // setamos lo datos
            restaurant?.let {
                withContext(Dispatchers.Main) {
                    titulo = it.titulo
                    descripcion = it.descripcio
                    imageUri = Uri.parse(it.image)
                    price = it.price.toString()
                    lat = it.lat
                    long = it.long
                }
            }
        }
    }

    fun updateRestaurant(onComplete: () -> Unit) {
        if (titulo.isBlank() || descripcion.isBlank() || imageUri == null || price.isBlank()) {
            return // Validación fallida
        }
        // hacemos el update one con los datos actuales de nuesto viewmodel
        viewModelScope.launch(Dispatchers.IO) {
            database.databaseQueries.UpdateOne(
                titulo = titulo,
                descripcio = descripcion,
                lat = lat,
                long = long,
                image = imageUri.toString(),
                price = price.toIntOrNull()?.toLong() ?: 0L,
                id = restaurantId
            )
            // para que vuelva a la pantalla anterior
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteRestaurant(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            database.databaseQueries.DeleteOne(restaurantId)//eliminamos de la base de datos
            withContext(Dispatchers.Main) {
                onComplete() // volvemos para atras
            }
        }
    }
}