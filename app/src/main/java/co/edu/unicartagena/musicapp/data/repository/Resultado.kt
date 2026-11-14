package co.edu.unicartagena.musicapp.data.repository

import co.edu.unicartagena.musicapp.data.remote.ErrorDto
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

/** Resultado de una llamada a la API: éxito con datos o error con un mensaje en español. */
sealed interface Resultado<out T> {
    data class Exito<T>(val datos: T) : Resultado<T>
    data class Error(val mensaje: String, val codigo: Int? = null) : Resultado<Nothing> {
        val sesionExpirada: Boolean get() = codigo == 401
    }
}

/** Ejecuta una llamada y convierte las excepciones de red/HTTP en [Resultado.Error]. */
suspend fun <T> llamarApi(bloque: suspend () -> T): Resultado<T> = try {
    Resultado.Exito(bloque())
} catch (e: HttpException) {
    val cuerpo = e.response()?.errorBody()?.string()
    val mensaje = runCatching { Gson().fromJson(cuerpo, ErrorDto::class.java)?.mensaje }.getOrNull()
    Resultado.Error(mensaje ?: "Error ${e.code()} del servidor", e.code())
} catch (e: IOException) {
    Resultado.Error("No hay conexión con el servidor. Revisa tu internet e intenta de nuevo.")
} catch (e: Exception) {
    Resultado.Error("Ocurrió un error inesperado: ${e.message}")
}
