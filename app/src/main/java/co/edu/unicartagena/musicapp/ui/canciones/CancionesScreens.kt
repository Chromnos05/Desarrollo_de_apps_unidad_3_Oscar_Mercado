package co.edu.unicartagena.musicapp.ui.canciones

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicartagena.musicapp.data.remote.CancionDto
import co.edu.unicartagena.musicapp.ui.components.Ayuda
import co.edu.unicartagena.musicapp.ui.components.BotonPrincipal
import co.edu.unicartagena.musicapp.ui.components.Campo
import co.edu.unicartagena.musicapp.ui.components.Encabezado
import co.edu.unicartagena.musicapp.ui.components.Seccion
import co.edu.unicartagena.musicapp.ui.components.SelectorFecha
import co.edu.unicartagena.musicapp.ui.components.SelectorRitmo
import co.edu.unicartagena.musicapp.ui.components.Tarjeta
import co.edu.unicartagena.musicapp.ui.components.appViewModel
import co.edu.unicartagena.musicapp.ui.theme.Gris
import co.edu.unicartagena.musicapp.ui.theme.Rojo
import co.edu.unicartagena.musicapp.ui.theme.RojoSuave
import co.edu.unicartagena.musicapp.ui.theme.Tinta
import co.edu.unicartagena.musicapp.ui.theme.Violeta
import co.edu.unicartagena.musicapp.ui.theme.VioletaSuave

/** Fila de una canción: nombre en negrita y datos secundarios en gris. */
@Composable
fun FilaCancion(c: CancionDto, onClick: (() -> Unit)? = null) {
    val modificador = Modifier.fillMaxWidth()
    val contenido: @Composable () -> Unit = {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(end = 12.dp)) { Text("🎵", fontSize = 26.sp) }
            Column(Modifier.weight(1f)) {
                Text(c.nombre, fontWeight = FontWeight.Bold, color = Tinta, fontSize = 16.sp)
                Text(
                    listOfNotNull(c.banda ?: c.interprete, c.ritmo, c.fechaLanzamiento).joinToString(" · "),
                    color = Gris, fontSize = 13.sp
                )
            }
            Text("${c.duracion / 60}:${(c.duracion % 60).toString().padStart(2, '0')}", color = Violeta,
                fontWeight = FontWeight.Bold)
        }
    }
    val colores = CardDefaults.cardColors(containerColor = Color.White)
    val forma = RoundedCornerShape(16.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modificador, shape = forma, colors = colores) { contenido() }
    } else {
        Card(modifier = modificador, shape = forma, colors = colores) { contenido() }
    }
}

/**
 * Lista de canciones. [refrescarPorSensor] es un contador que aumenta cada vez que el
 * acelerómetro detecta que el celular se agitó: al cambiar, la lista se recarga.
 */
@Composable
fun CancionesScreen(
    refrescarPorSensor: Int,
    onNueva: () -> Unit,
    onAbrir: (Long) -> Unit,
    onVolver: () -> Unit,
    onSesionExpirada: () -> Unit,
) {
    val vm = appViewModel { CancionesViewModel(it.canciones) }
    val contexto = LocalContext.current
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(refrescarPorSensor) {
        if (refrescarPorSensor > 0) {
            Toast.makeText(contexto, "Actualizando lista...", Toast.LENGTH_SHORT).show()
            vm.cargar()
        }
    }
    LaunchedEffect(vm.sesionExpirada) { if (vm.sesionExpirada) onSesionExpirada() }

    Scaffold(
        topBar = {
            Encabezado("Canciones", "Agita el celular para actualizar", onVolver) {
                IconButton(onClick = vm::cargar) { Icon(Icons.Filled.Refresh, "Actualizar", tint = Color.White) }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNueva, containerColor = Violeta, contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, null) }, text = { Text("Nueva") }
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = vm.filtro, onValueChange = { vm.filtro = it },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                placeholder = { Text("Buscar por nombre, banda o ritmo") },
                singleLine = true, shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            )
            vm.error?.let { Text(it, color = Rojo, fontSize = 13.sp) }
            if (vm.cargando && vm.canciones.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (vm.visibles.isEmpty()) {
                Ayuda("No hay canciones todavía. Toca \"Nueva\" para agregar la primera.",
                    Modifier.padding(top = 24.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(vm.visibles, key = { it.id ?: 0L }) { c -> FilaCancion(c) { c.id?.let(onAbrir) } }
                    item { Box(Modifier.padding(40.dp)) }
                }
            }
        }
    }
}

@Composable
fun CancionFormScreen(id: Long?, onListo: () -> Unit, onVolver: () -> Unit, onSesionExpirada: () -> Unit) {
    val vm = appViewModel { CancionFormViewModel(it.canciones, id) }
    val contexto = LocalContext.current
    var confirmarBorrado by remember { mutableStateOf(false) }
    LaunchedEffect(vm.sesionExpirada) { if (vm.sesionExpirada) onSesionExpirada() }
    val alTerminar: (String) -> Unit = { msg -> Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show(); onListo() }

    Scaffold(topBar = {
        Encabezado(if (vm.esNueva) "Nueva canción" else "Editar canción", null, onVolver) {
            if (!vm.esNueva) {
                IconButton(onClick = { confirmarBorrado = true }) {
                    Icon(Icons.Filled.Delete, "Eliminar", tint = Color.White)
                }
            }
        }
    }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Tarjeta {
                Seccion("Canción")
                Campo(vm.nombre, { vm.nombre = it }, "🎵 Nombre de la canción")
                SelectorRitmo(vm.ritmo, { vm.ritmo = it })
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Campo(vm.duracion, { vm.duracion = it }, "Duración (seg)", Modifier.weight(1f), KeyboardType.Number)
                    Campo(vm.posicion, { vm.posicion = it }, "Posición", Modifier.weight(1f), KeyboardType.Number)
                }
                Seccion("Créditos")
                Campo(vm.album, { vm.album = it }, "💿 Álbum")
                Campo(vm.banda, { vm.banda = it }, "🎸 Banda")
                Campo(vm.interprete, { vm.interprete = it }, "🎤 Intérprete")
                Campo(vm.autor, { vm.autor = it }, "✍ Autor")
                Seccion("Fecha de lanzamiento")
                SelectorFecha(vm.fecha, "Elegir fecha", { vm.fecha = it })
            }
            vm.error?.let { Text(it, color = Rojo, fontSize = 13.sp) }
            BotonPrincipal(if (vm.esNueva) "＋ GUARDAR" else "✎ ACTUALIZAR", { vm.guardar(alTerminar) },
                cargando = vm.cargando)
            if (!vm.esNueva) {
                BotonPrincipal("🗑 ELIMINAR", { confirmarBorrado = true }, color = RojoSuave, colorTexto = Rojo)
            }
            Box(Modifier.navigationBarsPadding())
        }
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("Eliminar canción") },
            text = { Text("¿Seguro que deseas eliminar \"${vm.nombre}\"?") },
            confirmButton = {
                TextButton(onClick = { confirmarBorrado = false; vm.eliminar(alTerminar) }) {
                    Text("Eliminar", color = Rojo)
                }
            },
            dismissButton = { TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") } },
            containerColor = VioletaSuave,
        )
    }
}
