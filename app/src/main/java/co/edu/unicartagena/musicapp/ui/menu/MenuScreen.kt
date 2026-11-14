package co.edu.unicartagena.musicapp.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicartagena.musicapp.ui.components.appViewModel
import co.edu.unicartagena.musicapp.ui.theme.DegradadoMarca
import co.edu.unicartagena.musicapp.ui.theme.Indigo
import co.edu.unicartagena.musicapp.ui.theme.Rojo
import co.edu.unicartagena.musicapp.ui.theme.Rosa
import co.edu.unicartagena.musicapp.ui.theme.Tinta
import co.edu.unicartagena.musicapp.ui.theme.Turquesa
import co.edu.unicartagena.musicapp.ui.theme.Violeta
import androidx.lifecycle.ViewModel
import co.edu.unicartagena.musicapp.data.repository.AuthRepository

class MenuViewModel(private val auth: AuthRepository) : ViewModel() {
    val nombre: String get() = auth.nombreActual()
    val email: String get() = auth.emailActual()
    fun cerrarSesion() = auth.cerrarSesion()
}

@Composable
fun MenuScreen(
    onCanciones: () -> Unit,
    onPerfil: () -> Unit,
    onReporteCanciones: () -> Unit,
    onReporteUsuarios: () -> Unit,
    onSalir: (cerrarSesion: Boolean) -> Unit,
) {
    val vm = appViewModel { MenuViewModel(it.auth) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().background(DegradadoMarca).statusBarsPadding().padding(vertical = 32.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hola, ${vm.nombre.substringBefore(' ').ifBlank { "👋" }} 👋", color = Color.White,
                    fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(vm.email, color = Color(0xFFEDE9FE), fontSize = 14.sp)
            }
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("¿Qué quieres hacer hoy?", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Tinta)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Mosaico("🎵", "Canciones", Violeta, onCanciones, Modifier.weight(1f))
                Mosaico("👤", "Mi perfil", Rosa, onPerfil, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Mosaico("📊", "Reportes de\ncanciones", Indigo, onReporteCanciones, Modifier.weight(1f))
                Mosaico("👥", "Reportes de\nusuarios", Turquesa, onReporteUsuarios, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { onSalir(false) }, modifier = Modifier.fillMaxWidth()) {
                Text("Salir (puedes volver con tu huella)", color = Violeta)
            }
            TextButton(onClick = { vm.cerrarSesion(); onSalir(true) }, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión en este dispositivo", color = Rojo, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Mosaico(emoji: String, texto: String, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(emoji, fontSize = 34.sp)
            Text(texto, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
        }
    }
}
