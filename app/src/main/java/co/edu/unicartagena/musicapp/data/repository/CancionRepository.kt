package co.edu.unicartagena.musicapp.data.repository

import co.edu.unicartagena.musicapp.data.remote.CancionDto
import co.edu.unicartagena.musicapp.data.remote.MusicApiService

/** CRUD y reportes de la tabla Canciones. */
class CancionRepository(private val api: MusicApiService) {

    suspend fun listar(): Resultado<List<CancionDto>> = llamarApi { api.listarCanciones() }

    suspend fun obtener(id: Long): Resultado<CancionDto> = llamarApi { api.obtenerCancion(id) }

    suspend fun guardar(cancion: CancionDto): Resultado<CancionDto> = llamarApi {
        val id = cancion.id
        if (id == null) api.crearCancion(cancion) else api.actualizarCancion(id, cancion)
    }

    suspend fun eliminar(id: Long): Resultado<String> = llamarApi { api.eliminarCancion(id).mensaje }

    suspend fun reportePorRitmo(ritmo: String) = llamarApi { api.reportePorRitmo(ritmo) }

    suspend fun reportePorFechas(desde: String, hasta: String) = llamarApi { api.reportePorFechas(desde, hasta) }

    suspend fun reportePorDuracion(min: Int, max: Int) = llamarApi { api.reportePorDuracion(min, max) }
}
