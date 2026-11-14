package co.edu.unicartagena.musicapp.ui.canciones

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicartagena.musicapp.data.remote.CancionDto
import co.edu.unicartagena.musicapp.data.repository.CancionRepository
import co.edu.unicartagena.musicapp.data.repository.Resultado
import kotlinx.coroutines.launch

/** Lista de canciones (READ). */
class CancionesViewModel(private val repo: CancionRepository) : ViewModel() {
    var canciones by mutableStateOf<List<CancionDto>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
    var sesionExpirada by mutableStateOf(false)
        private set
    var filtro by mutableStateOf("")

    val visibles: List<CancionDto>
        get() = if (filtro.isBlank()) canciones else canciones.filter {
            listOfNotNull(it.nombre, it.banda, it.interprete, it.ritmo).any { campo -> campo.contains(filtro, true) }
        }

    fun cargar() {
        viewModelScope.launch {
            cargando = true
            when (val r = repo.listar()) {
                is Resultado.Exito -> { canciones = r.datos; error = null }
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }
}

/** Formulario para crear, ver, actualizar y eliminar una canción. */
class CancionFormViewModel(private val repo: CancionRepository, private val id: Long?) : ViewModel() {
    var nombre by mutableStateOf("")
    var ritmo by mutableStateOf("Rock")
    var duracion by mutableStateOf("")
    var album by mutableStateOf("")
    var posicion by mutableStateOf("")
    var banda by mutableStateOf("")
    var interprete by mutableStateOf("")
    var autor by mutableStateOf("")
    var fecha by mutableStateOf("")
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
    var sesionExpirada by mutableStateOf(false)
        private set

    val esNueva = id == null

    init {
        if (id != null) cargar(id)
    }

    private fun cargar(id: Long) {
        viewModelScope.launch {
            cargando = true
            when (val r = repo.obtener(id)) {
                is Resultado.Exito -> with(r.datos) {
                    this@CancionFormViewModel.nombre = nombre
                    this@CancionFormViewModel.ritmo = ritmo
                    this@CancionFormViewModel.duracion = duracion.toString()
                    this@CancionFormViewModel.album = album.orEmpty()
                    this@CancionFormViewModel.posicion = posicionEnAlbum?.toString().orEmpty()
                    this@CancionFormViewModel.banda = banda.orEmpty()
                    this@CancionFormViewModel.interprete = interprete.orEmpty()
                    this@CancionFormViewModel.autor = autor.orEmpty()
                    this@CancionFormViewModel.fecha = fechaLanzamiento.orEmpty()
                }
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }

    fun guardar(onExito: (String) -> Unit) {
        val segundos = duracion.trim().toIntOrNull()
        val pos = posicion.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        error = when {
            nombre.isBlank() -> "El nombre es obligatorio"
            segundos == null || segundos <= 0 -> "La duración debe ser un número de segundos mayor que 0"
            posicion.isNotBlank() && (pos == null || pos <= 0) -> "La posición en el álbum debe ser un número mayor que 0"
            fecha.isBlank() -> "Elige la fecha de lanzamiento"
            else -> null
        }
        if (error != null || segundos == null) return
        val cancion = CancionDto(
            id = id, nombre = nombre.trim(), ritmo = ritmo, duracion = segundos,
            album = album.trim().ifEmpty { null }, posicionEnAlbum = pos,
            banda = banda.trim().ifEmpty { null }, interprete = interprete.trim().ifEmpty { null },
            autor = autor.trim().ifEmpty { null }, fechaLanzamiento = fecha,
        )
        viewModelScope.launch {
            cargando = true
            when (val r = repo.guardar(cancion)) {
                is Resultado.Exito -> onExito(if (esNueva) "Canción guardada" else "Canción actualizada")
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }

    fun eliminar(onExito: (String) -> Unit) {
        val actual = id ?: return
        viewModelScope.launch {
            cargando = true
            when (val r = repo.eliminar(actual)) {
                is Resultado.Exito -> onExito(r.datos)
                is Resultado.Error -> { error = r.mensaje; sesionExpirada = r.sesionExpirada }
            }
            cargando = false
        }
    }
}
