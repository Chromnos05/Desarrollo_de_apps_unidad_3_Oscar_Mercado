package co.edu.unicartagena.musicapp.data.local

import android.content.Context

/**
 * Guarda la sesión en el dispositivo (SharedPreferences): token JWT, datos del usuario
 * y la hora del login. El token de la API dura 24 h; se considera válido hasta 23 h.
 */
class SessionStore(context: Context) {

    private val prefs = context.getSharedPreferences("sesion_musicapp", Context.MODE_PRIVATE)

    val token: String? get() = prefs.getString(TOKEN, null)
    val email: String get() = prefs.getString(EMAIL, "") ?: ""
    val nombre: String get() = prefs.getString(NOMBRE, "") ?: ""

    fun guardar(token: String, email: String, nombre: String) {
        prefs.edit()
            .putString(TOKEN, token)
            .putString(EMAIL, email)
            .putString(NOMBRE, nombre)
            .putLong(HORA_LOGIN, System.currentTimeMillis())
            .apply()
    }

    fun actualizarNombre(nombre: String) {
        prefs.edit().putString(NOMBRE, nombre).apply()
    }

    /** Hay una sesión guardada que todavía no vence (se usa para entrar con huella). */
    fun tieneSesionVigente(): Boolean {
        val horaLogin = prefs.getLong(HORA_LOGIN, 0L)
        return !token.isNullOrBlank() && System.currentTimeMillis() - horaLogin < VIGENCIA_MS
    }

    fun cerrar() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val TOKEN = "token"
        const val EMAIL = "email"
        const val NOMBRE = "nombre"
        const val HORA_LOGIN = "hora_login"
        const val VIGENCIA_MS = 23L * 60 * 60 * 1000
    }
}
