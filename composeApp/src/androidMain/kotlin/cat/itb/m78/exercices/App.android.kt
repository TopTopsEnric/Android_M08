package cat.itb.m78.exercices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cat.itb.m78.exercices.Projecto.RamenApp

// Configuracionde colores
val ramenColors = lightColorScheme(
    primary = Color(0xFFD32F2F), // Rojo fuerte, el color principal
    onPrimary = Color.White, // Texto blanco sobre fondo rojo
    secondary = Color(0xFFFFC107), // Amarillo vibrante (acento)
    onSecondary = Color.Black, // Texto negro sobre fondo amarillo
    background = Color(0xFFFAFAFA), // Fondo suave claro
    onBackground = Color.Black, // Texto negro sobre fondo claro
    surface = Color(0xFFFFEB3B), // Amarillo suave para superficies como tarjetas
    onSurface = Color.Black, // Texto negro sobre superficies amarillas
    error = Color(0xFFB71C1C), // Rojo oscuro para errores
    onError = Color.White, // Texto blanco sobre fondo rojo de error
    primaryContainer = Color(0xFFFF7043), // Un rojo más suave para contenedores
    onPrimaryContainer = Color.White, // Texto blanco sobre fondo rojo suave
    surfaceVariant = Color(0xFFEFEBE9), // Variante suave de fondo para divisores o tarjetas
    outline = Color(0xFF9E9E9E) // Gris suave para bordes y divisores
)

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Aplicar el tema global (colores, tipografía, etc.)
            MaterialTheme(
                colorScheme = ramenColors, // Aquí estás usando los colores personalizados
            ) {
                // Surface para contener y darle fondo al contenido
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Usando el color de fondo del tema
                ) {
                    // Aquí lanzas tu aplicación principal
                    RamenApp(database = database)
                }
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() { App() }
