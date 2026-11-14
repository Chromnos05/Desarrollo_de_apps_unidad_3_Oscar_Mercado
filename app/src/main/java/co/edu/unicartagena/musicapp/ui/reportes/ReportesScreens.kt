package co.edu.unicartagena.musicapp.ui.reportes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicartagena.musicapp.data.remote.CancionDto
import co.edu.unicartagena.musicapp.data.remote.UsuarioDto
import co.edu.unicartagena.musicapp.data.repository.AuthRepository
import co.edu.unicartagena.musicapp.data.repository.CancionRepository
import co.edu.unicartagena.musicapp.data.repository.Resultado
import co.edu.unicartagena.musicapp.ui.canciones.FilaCancion
import co.edu.unicartagena.musicapp.ui.components.Ayuda
import co.edu.unicartagena.musicapp.ui.components.BotonPrincipal
import co.edu.unicartagena.musicapp.ui.components.Campo
import co.edu.unicartagena.musicapp.ui.components.Encabezado
import co.edu.unicartagena.musicapp.ui.components.SelectorFecha
import co.edu.unicartagena.musicapp.ui.components.SelectorRitmo
import co.edu.unicartagena.musicapp.ui.components.Tarjeta
import co.edu.unicartagena.musicapp.ui.components.appViewModel
import co.edu.unicartagena.musicapp.ui.theme.Gris
import co.edu.unicartagena.musicapp.ui.theme.Rojo
import co.edu.unicartagena.musicapp.ui.theme.Tinta
import co.edu.unicartagena.musicapp.ui.theme.Violeta
import kotlinx.coroutines.launch

/** Estado común de un reporte: título, resultados, error y carga. */
abstract class ReporteViewModel<T> : ViewModel() {
    var titulo by mutableStateOf("")
        protected set
    var resultados by mutableStateOf<List<T>?>(null)
        protected set
    var cargando by mutableStateOf(false)
        protected set
    var error by mutableStateOf<String?>(null)
    var sesionExpirada by mutableStateOf(false)
        protected set

    protected fun ejecutar(nuevoTitulo: String, llamada: suspend () -> Resultado<List<T>>) {
        viewModelScope.launch {
            cargando = true
            error = null
            when (val r = llamada()) {
                is Resultado.Exito -> { titulo = nuevoTitulo; resultados = r.datos }
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }
}

class ReporteCancionesViewModel(private val repo: CancionRepository) : ReporteViewModel<CancionDto>() {
    var ritmo by mutableStateOf("Rock")
    var desde by mutableStateOf("")
    var hasta by mutableStateOf("")
    var minimo by mutableStateOf("")
    var maximo by mutableStateOf("")

    fun porRitmo() = ejecutar("Ritmo $ritmo") { repo.reportePorRitmo(ritmo) }

    fun porFechas() {
        if (desde.isBlank() || hasta.isBlank()) { error = "Elige las dos fechas"; return }
        ejecutar("Lanzadas entre $desde y $hasta") { repo.reportePorFechas(desde, hasta) }
    }

    fun porDuracion() {
        val min = minimo.trim().toIntOrNull()
        val max = maximo.trim().toIntOrNull()
        if (min == null || max == null) { error = "Escribe la duración mínima y máxima en segundos"; return }
        ejecutar("Duración entre $min y $max s") { repo.reportePorDuracion(min, max) }
    }
}

class ReporteUsuariosViewModel(private val auth: AuthRepository) : ReporteViewModel<UsuarioDto>() {
    var username by mutableStateOf("")
    var prefijo by mutableStateOf("")

    fun porUsername() {
        if (username.isBlank()) { error = "Escribe un username"; return }
        ejecutar("Username \"${username.trim()}\"") { auth.reportePorUsername(username) }
    }

    fun porNombre() {
        if (prefijo.isBlank()) { error = "Escribe el inicio del nombre"; return }
        ejecutar("Nombres que empiezan por \"${prefijo.trim()}\"") { auth.reportePorNombre(prefijo) }
    }
}

@Composable
private fun TituloReporte(texto: String) {
    Text(texto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Tinta)
}

@Composable
private fun ResumenResultados(titulo: String, cantidad: Int?) {
    if (cantidad != null) {
        Text("RESULTADOS · $titulo · $cantidad", color = Violeta, fontWeight = FontWeight.Bold, fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp))
        if (cantidad == 0) Ayuda("No hay registros que cumplan el filtro.")
    }
}

@Composable
fun ReporteCancionesScreen(onVolver: () -> Unit, onSesionExpirada: () -> Unit) {
    val vm = appViewModel { ReporteCancionesViewModel(it.canciones) }
    LaunchedEffect(vm.sesionExpirada) { if (vm.sesionExpirada) onSesionExpirada() }
    Scaffold(topBar = { Encabezado("Reportes de canciones", "Filtra tu catálogo", onVolver) }) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Tarjeta(Modifier.padding(top = 16.dp)) {
                    TituloReporte("🎼  Reporte 1 · Por ritmo")
                    SelectorRitmo(vm.ritmo, { vm.ritmo = it })
                    BotonPrincipal("Ver canciones de este ritmo", vm::porRitmo, cargando = vm.cargando)
                }
            }
            item {
                Tarjeta {
                    TituloReporte("📅  Reporte 2 · Por fecha de lanzamiento")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SelectorFecha(vm.desde, "Desde", { vm.desde = it }, Modifier.weight(1f))
                        SelectorFecha(vm.hasta, "Hasta", { vm.hasta = it }, Modifier.weight(1f))
                    }
                    BotonPrincipal("Ver canciones en ese rango", vm::porFechas, cargando = vm.cargando)
                }
            }
            item {
                Tarjeta {
                    TituloReporte("⏱  Reporte 3 · Por duración (segundos)")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Campo(vm.minimo, { vm.minimo = it }, "Mínimo", Modifier.weight(1f), KeyboardType.Number)
                        Campo(vm.maximo, { vm.maximo = it }, "Máximo", Modifier.weight(1f), KeyboardType.Number)
                    }
                    BotonPrincipal("Ver canciones con esa duración", vm::porDuracion, cargando = vm.cargando)
                }
            }
            item {
                vm.error?.let { Text(it, color = Rojo, fontSize = 13.sp) }
                ResumenResultados(vm.titulo, vm.resultados?.size)
            }
            items(vm.resultados.orEmpty(), key = { it.id ?: 0L }) { FilaCancion(it) }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ReporteUsuariosScreen(onVolver: () -> Unit, onSesionExpirada: () -> Unit) {
    val vm = appViewModel { ReporteUsuariosViewModel(it.auth) }
    LaunchedEffect(vm.sesionExpirada) { if (vm.sesionExpirada) onSesionExpirada() }
    Scaffold(topBar = { Encabezado("Reportes de usuarios", "Busca usuarios registrados", onVolver) }) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Tarjeta(Modifier.padding(top = 16.dp)) {
                    TituloReporte("🔎  Reporte 1 · Por username exacto")
                    Campo(vm.username, { vm.username = it }, "Ej: oscar")
                    BotonPrincipal("Buscar username", vm::porUsername, cargando = vm.cargando)
                }
            }
            item {
                Tarjeta {
                    TituloReporte("🔤  Reporte 2 · Nombre que empieza por...")
                    Campo(vm.prefijo, { vm.prefijo = it }, "Ej: Ana")
                    BotonPrincipal("Buscar por nombre", vm::porNombre, cargando = vm.cargando)
                }
            }
            item {
                vm.error?.let { Text(it, color = Rojo, fontSize = 13.sp) }
                ResumenResultados(vm.titulo, vm.resultados?.size)
            }
            items(vm.resultados.orEmpty(), key = { it.id }) { u ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(14.dp)) {
                        Text("👤  ", fontSize = 22.sp)
                        androidx.compose.foundation.layout.Column {
                            Text(u.nombre, fontWeight = FontWeight.Bold, color = Tinta)
                            Text("@${u.username} · ${u.email}", color = Gris, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
