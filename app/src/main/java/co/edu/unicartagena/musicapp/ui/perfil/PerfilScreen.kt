package co.edu.unicartagena.musicapp.ui.perfil

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicartagena.musicapp.data.repository.AuthRepository
import co.edu.unicartagena.musicapp.data.repository.Resultado
import co.edu.unicartagena.musicapp.ui.components.BotonPrincipal
import co.edu.unicartagena.musicapp.ui.components.Campo
import co.edu.unicartagena.musicapp.ui.components.Encabezado
import co.edu.unicartagena.musicapp.ui.components.Seccion
import co.edu.unicartagena.musicapp.ui.components.Tarjeta
import co.edu.unicartagena.musicapp.ui.components.appViewModel
import co.edu.unicartagena.musicapp.ui.theme.Gris
import co.edu.unicartagena.musicapp.ui.theme.Rojo
import co.edu.unicartagena.musicapp.ui.theme.RojoSuave
import kotlinx.coroutines.launch

/** CRUD de la tabla Usuario sobre la cuenta propia: ver, actualizar y eliminar. */
class PerfilViewModel(private val auth: AuthRepository) : ViewModel() {
    var username by mutableStateOf("")
    var nombre by mutableStateOf("")
    var email by mutableStateOf(auth.emailActual())
        private set
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
    var sesionExpirada by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            cargando = true
            when (val r = auth.miPerfil()) {
                is Resultado.Exito -> { username = r.datos.username; nombre = r.datos.nombre; email = r.datos.email }
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }

    fun guardar(onExito: () -> Unit) {
        if (username.isBlank() || nombre.isBlank()) { error = "Completa username y nombre"; return }
        viewModelScope.launch {
            cargando = true
            when (val r = auth.actualizarPerfil(username, nombre)) {
                is Resultado.Exito -> { error = null; onExito() }
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }

    fun eliminarCuenta(onExito: () -> Unit) {
        viewModelScope.launch {
            cargando = true
            when (val r = auth.eliminarCuenta()) {
                is Resultado.Exito -> onExito()
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }
}

@Composable
fun PerfilScreen(onVolver: () -> Unit, onCuentaEliminada: () -> Unit, onSesionExpirada: () -> Unit) {
    val vm = appViewModel { PerfilViewModel(it.auth) }
    val contexto = LocalContext.current
    var confirmar by remember { mutableStateOf(false) }
    LaunchedEffect(vm.sesionExpirada) { if (vm.sesionExpirada) onSesionExpirada() }

    Scaffold(topBar = { Encabezado("Mi perfil", vm.email, onVolver) }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("👤", fontSize = 64.sp)
            Tarjeta {
                Seccion("Nombre de usuario")
                Campo(vm.username, { vm.username = it }, "username")
                Seccion("Nombre completo")
                Campo(vm.nombre, { vm.nombre = it }, "Nombre y apellidos")
                vm.error?.let { Text(it, color = Rojo, fontSize = 13.sp) }
                BotonPrincipal("GUARDAR CAMBIOS", {
                    vm.guardar { Toast.makeText(contexto, "Perfil guardado", Toast.LENGTH_SHORT).show() }
                }, cargando = vm.cargando)
            }
            BotonPrincipal("Eliminar mi cuenta", { confirmar = true }, color = RojoSuave, colorTexto = Rojo)
            Text("La contraseña se guarda cifrada en el servidor y nunca se muestra.", color = Gris,
                fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("Eliminar cuenta") },
            text = { Text("Se eliminarán tu cuenta y tus datos. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    vm.eliminarCuenta {
                        Toast.makeText(contexto, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                        onCuentaEliminada()
                    }
                }) { Text("Eliminar", color = Rojo) }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
        )
    }
}
