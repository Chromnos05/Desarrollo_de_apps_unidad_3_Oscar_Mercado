package co.edu.unicartagena.musicapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Paleta de MusicApp (la misma de las unidades 1 y 2)
val Violeta = Color(0xFF6D28D9)
val VioletaOscuro = Color(0xFF4C1D95)
val VioletaSuave = Color(0xFFEDE9FE)
val Rosa = Color(0xFFDB2777)
val Indigo = Color(0xFF4F46E5)
val Turquesa = Color(0xFF0D9488)
val Rojo = Color(0xFFDC2626)
val RojoSuave = Color(0xFFFEE2E2)
val Fondo = Color(0xFFF5F3FF)
val Tinta = Color(0xFF1F2937)
val Gris = Color(0xFF6B7280)
val GrisCampo = Color(0xFFF3F4F6)

val DegradadoMarca = Brush.linearGradient(listOf(Color(0xFF7C3AED), Rosa))

private val Colores = lightColorScheme(
    primary = Violeta,
    onPrimary = Color.White,
    primaryContainer = VioletaSuave,
    onPrimaryContainer = VioletaOscuro,
    secondary = Rosa,
    onSecondary = Color.White,
    tertiary = Turquesa,
    background = Fondo,
    onBackground = Tinta,
    surface = Color.White,
    onSurface = Tinta,
    surfaceVariant = GrisCampo,
    onSurfaceVariant = Gris,
    error = Rojo,
)

private val Formas = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun MusicAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colores, shapes = Formas, content = content)
}
