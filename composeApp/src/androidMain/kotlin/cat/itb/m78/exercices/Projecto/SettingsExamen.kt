package cat.itb.m78.exercices.Projecto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class fotolSettings(
    var imatgereciente : String = "facil",
)

data object FotoSettingsManager{
    private var settings = fotolSettings()
    fun update(newSettings: fotolSettings){
        settings = newSettings
    }
    fun get() = settings
}

class SettingsViewModel : ViewModel() {
    var foto by mutableStateOf(FotoSettingsManager.get().imatgereciente)

    fun saveSettings() {
        val newSettings = fotolSettings(
            imatgereciente = foto.trim(), // Normalizar el string

        )
        FotoSettingsManager.update(newSettings)
    }

    fun updatefoto(imatge: String) {
        foto = imatge
    }

}