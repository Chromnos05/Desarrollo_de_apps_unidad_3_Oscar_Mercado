package co.edu.unicartagena.musicapp.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicartagena.musicapp.data.repository.AuthRepository
import co.edu.unicartagena.musicapp.data.repository.Resultado
import kotlinx.coroutines.launch

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {
    var usuario by mutableStateOf("")
    var clave by mutableStateOf("")
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)

    /** Hay una sesión guardada vigente: se puede entrar con la huella. */
    fun puedeUsarHuella() = auth.tieneSesionVigente()

    fun entrar(onExito: () -> Unit) {
        if (usuario.isBlank() || clave.isBlank()) {
            error = "Escribe tu correo (o usuario) y tu contraseña"
            return
        }
        viewModelScope.launch {
            cargando = true
            error = null
            when (val r = auth.login(usuario, clave)) {
                is Resultado.Exito -> { clave = ""; onExito() }
                is Resultado.Error -> error = r.mensaje
            }
            cargando = false
        }
    }
}

class RegistroViewModel(private val auth: AuthRepository) : ViewModel() {
    var nombre by mutableStateOf("")
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var clave by mutableStateOf("")
    var clave2 by mutableStateOf("")
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)

    fun registrar(onExito: () -> Unit) {
        error = when {
            listOf(nombre, username, email, clave).any { it.isBlank() } -> "Completa todos los campos"
            clave != clave2 -> "Las contraseñas no coinciden"
            clave.length < 6 -> "La contraseña debe tener mínimo 6 caracteres"
            else -> null
        }
        if (error != null) return
        viewModelScope.launch {
            cargando = true
            when (val r = auth.registrar(username, nombre, email, clave)) {
                is Resultado.Exito -> onExito()
                is Resultado.Error -> error = r.mensaje
            }
            cargando = false
        }
    }
}

class RecuperarViewModel(private val auth: AuthRepository, emailInicial: String) : ViewModel() {
    var email by mutableStateOf(emailInicial)
    var codigo by mutableStateOf("")
    var nueva by mutableStateOf("")
    var nueva2 by mutableStateOf("")
    var codigoEnviado by mutableStateOf(false)
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
    var error by mutableStateOf<String?>(null)

    fun enviarCodigo() {
        if (email.isBlank()) { error = "Escribe tu correo"; return }
        viewModelScope.launch {
            cargando = true
            error = null
            when (val r = auth.recuperar(email)) {
                is Resultado.Exito -> {
                    codigoEnviado = true
                    mensaje = r.datos + " Revisa también la carpeta de spam. El código vence en 15 minutos."
                }
                is Resultado.Error -> error = r.mensaje
            }
            cargando = false
        }
    }

    fun cambiarClave(onExito: (String) -> Unit) {
        error = when {
            email.isBlank() || codigo.isBlank() || nueva.isBlank() -> "Completa el correo, el código y la nueva contraseña"
            nueva != nueva2 -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) return
        viewModelScope.launch {
            cargando = true
            when (val r = auth.restablecer(email, codigo, nueva)) {
                is Resultado.Exito -> onExito(r.datos)
                is Resultado.Error -> error = r.mensaje
            }
            cargando = false
        }
    }
}
