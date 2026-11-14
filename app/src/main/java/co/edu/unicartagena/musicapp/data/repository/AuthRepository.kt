package co.edu.unicartagena.musicapp.data.repository

import co.edu.unicartagena.musicapp.data.local.SessionStore
import co.edu.unicartagena.musicapp.data.remote.LoginRequest
import co.edu.unicartagena.musicapp.data.remote.MusicApiService
import co.edu.unicartagena.musicapp.data.remote.PerfilRequest
import co.edu.unicartagena.musicapp.data.remote.RecuperarRequest
import co.edu.unicartagena.musicapp.data.remote.RegistroRequest
import co.edu.unicartagena.musicapp.data.remote.RestablecerRequest
import co.edu.unicartagena.musicapp.data.remote.SesionDto
import co.edu.unicartagena.musicapp.data.remote.UsuarioDto

/** Registro, login, recuperación de clave y CRUD/reportes de la tabla Usuario. */
class AuthRepository(private val api: MusicApiService, private val session: SessionStore) {

    suspend fun login(usuario: String, password: String): Resultado<SesionDto> =
        llamarApi { api.login(LoginRequest(usuario.trim(), password)) }.also(::guardarSesion)

    suspend fun registrar(username: String, nombre: String, email: String, password: String): Resultado<SesionDto> =
        llamarApi { api.registrar(RegistroRequest(username.trim(), nombre.trim(), email.trim(), password)) }
            .also(::guardarSesion)

    suspend fun recuperar(email: String): Resultado<String> =
        llamarApi { api.recuperar(RecuperarRequest(email.trim())).mensaje }

    suspend fun restablecer(email: String, codigo: String, nueva: String): Resultado<String> =
        llamarApi { api.restablecer(RestablecerRequest(email.trim(), codigo.trim(), nueva)).mensaje }

    suspend fun miPerfil(): Resultado<UsuarioDto> = llamarApi { api.miPerfil() }

    suspend fun actualizarPerfil(username: String, nombre: String): Resultado<UsuarioDto> =
        llamarApi { api.actualizarPerfil(PerfilRequest(username.trim(), nombre.trim())) }
            .also { if (it is Resultado.Exito) session.actualizarNombre(it.datos.nombre) }

    suspend fun eliminarCuenta(): Resultado<String> =
        llamarApi { api.eliminarCuenta().mensaje }.also { if (it is Resultado.Exito) session.cerrar() }

    suspend fun reportePorUsername(username: String): Resultado<List<UsuarioDto>> =
        llamarApi { api.reportePorUsername(username.trim()) }

    suspend fun reportePorNombre(prefijo: String): Resultado<List<UsuarioDto>> =
        llamarApi { api.reportePorNombre(prefijo.trim()) }

    fun tieneSesionVigente() = session.tieneSesionVigente()
    fun emailActual() = session.email
    fun nombreActual() = session.nombre
    fun cerrarSesion() = session.cerrar()

    private fun guardarSesion(r: Resultado<SesionDto>) {
        if (r is Resultado.Exito) session.guardar(r.datos.token, r.datos.usuario.email, r.datos.usuario.nombre)
    }
}
