package co.edu.unicartagena.musicapp.data.remote

/** Objetos que viajan en JSON entre la app y la API REST. */

data class LoginRequest(val usuario: String, val password: String)

data class RegistroRequest(val username: String, val nombre: String, val email: String, val password: String)

data class RecuperarRequest(val email: String)

data class RestablecerRequest(val email: String, val codigo: String, val nuevaPassword: String)

data class PerfilRequest(val username: String, val nombre: String)

data class UsuarioDto(val id: Long, val username: String, val nombre: String, val email: String)

data class SesionDto(val token: String, val usuario: UsuarioDto)

data class CancionDto(
    val id: Long? = null,
    val nombre: String,
    val ritmo: String,
    val duracion: Int,
    val album: String? = null,
    val posicionEnAlbum: Int? = null,
    val banda: String? = null,
    val interprete: String? = null,
    val autor: String? = null,
    val fechaLanzamiento: String? = null,
)

data class MensajeDto(val mensaje: String)

data class ErrorDto(val estado: Int? = null, val error: String? = null, val mensaje: String? = null)
