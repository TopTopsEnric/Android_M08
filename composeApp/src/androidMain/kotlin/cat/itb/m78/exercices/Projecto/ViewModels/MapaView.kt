package cat.itb.m78.exercices.Projecto.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.itb.m78.exercices.db.Database
import databases.MyMarcador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val database: Database) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<MyMarcador>>(emptyList()) // lista mutbale pero privada
    val restaurants: StateFlow<List<MyMarcador>> = _restaurants // lista public pero no editable

    init {
        loadRestaurants() // cargamos cuando se iniciliza
    }

     fun loadRestaurants() {
        viewModelScope.launch(Dispatchers.IO) {
            _restaurants.value = database.databaseQueries.selectAll().executeAsList()// cargamos la lista
        }
    }

    // Recargar datos cuando sea necesario (por ejemplo, después de añadir o editar)
    fun refreshData() {
        loadRestaurants() // recargar cuando queramos para el refresh del mapa
    }
}