package co.edu.unicartagena.musicapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicartagena.musicapp.R
import co.edu.unicartagena.musicapp.ui.components.BotonPrincipal
import co.edu.unicartagena.musicapp.ui.components.Campo
import co.edu.unicartagena.musicapp.ui.components.Seccion
import co.edu.unicartagena.musicapp.ui.components.Tarjeta
import co.edu.unicartagena.musicapp.ui.components.appViewModel
import co.edu.unicartagena.musicapp.ui.theme.DegradadoMarca
import co.edu.unicartagena.musicapp.ui.theme.Rojo
import co.edu.unicartagena.musicapp.ui.theme.Rosa
import co.edu.unicartagena.musicapp.ui.theme.Turquesa
import co.edu.unicartagena.musicapp.ui.theme.Violeta
import co.edu.unicartagena.musicapp.ui.theme.VioletaSuave

/** Fondo con degradado y contenido centrado (pantallas de acceso). */
@Composable
private fun FondoMarca(titulo: String, subtitulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(DegradadoMarca)) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painterResource(R.drawable.logo), contentDescription = "Logo de MusicApp",
                modifier = Modifier.size(88.dp).clip(RoundedCornerShape(22.dp))
            )
            Spacer(Modifier.height(12.dp))
            Text(titulo, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text(subtitulo, color = Color(0xFFEDE9FE), fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            contenido()
        }
    }
}

@Composable
private fun MensajeError(error: String?) {
    if (error != null) Text(error, color = Rojo, fontSize = 13.sp)
}

@Composable
fun LoginScreen(
    onEntrar: () -> Unit,
    onRegistro: () -> Unit,
    onRecuperar: (String) -> Unit,
    onHuella: () -> Unit,
) {
    val vm = appViewModel { LoginViewModel(it.auth) }
    FondoMarca("MusicApp", "Tu catálogo de canciones, siempre contigo") {
        Tarjeta {
            Text("Iniciar sesión", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Campo(vm.usuario, { vm.usuario = it }, "Correo o nombre de usuario", teclado = KeyboardType.Email)
            Campo(vm.clave, { vm.clave = it }, "Contraseña", esClave = true)
            MensajeError(vm.error)
            BotonPrincipal("INICIAR SESIÓN", { vm.entrar(onEntrar) }, cargando = vm.cargando)
            if (vm.puedeUsarHuella()) {
                BotonPrincipal("👆  Entrar con huella digital", onHuella, color = VioletaSuave, colorTexto = Violeta)
            }
            TextButton(onClick = { onRecuperar(vm.usuario.takeIf { "@" in it } ?: "") },
                modifier = Modifier.fillMaxWidth()) {
                Text("¿Olvidaste tu contraseña?", color = Violeta)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta?", color = Color(0xFFEDE9FE))
            TextButton(onClick = onRegistro) { Text("Regístrate", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun RegistroScreen(onRegistrado: () -> Unit, onVolver: () -> Unit) {
    val vm = appViewModel { RegistroViewModel(it.auth) }
    val contexto = LocalContext.current
    FondoMarca("Crea tu cuenta", "Solo toma un minuto") {
        Tarjeta {
            Seccion("Tus datos")
            Campo(vm.nombre, { vm.nombre = it }, "Nombre completo")
            Campo(vm.username, { vm.username = it }, "Nombre de usuario")
            Campo(vm.email, { vm.email = it }, "Correo electrónico", teclado = KeyboardType.Email)
            Seccion("Seguridad")
            Campo(vm.clave, { vm.clave = it }, "Contraseña (mínimo 6)", esClave = true)
            Campo(vm.clave2, { vm.clave2 = it }, "Repite la contraseña", esClave = true)
            MensajeError(vm.error)
            BotonPrincipal("CREAR CUENTA", {
                vm.registrar {
                    Toast.makeText(contexto, "¡Cuenta creada!", Toast.LENGTH_SHORT).show()
                    onRegistrado()
                }
            }, cargando = vm.cargando)
            TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Ya tengo cuenta · Volver", color = Violeta)
            }
        }
    }
}

@Composable
fun RecuperarScreen(emailInicial: String, onListo: () -> Unit, onVolver: () -> Unit) {
    val vm = appViewModel { RecuperarViewModel(it.auth, emailInicial) }
    val contexto = LocalContext.current
    FondoMarca("Recuperar contraseña", "Te enviaremos un código a tu correo") {
        Tarjeta {
            Seccion("Paso 1 · Solicita el código")
            Campo(vm.email, { vm.email = it }, "Correo electrónico", teclado = KeyboardType.Email)
            BotonPrincipal("ENVIAR CÓDIGO", vm::enviarCodigo, cargando = vm.cargando && !vm.codigoEnviado)
            vm.mensaje?.let { Text(it, color = Turquesa, fontSize = 13.sp) }
            Seccion("Paso 2 · Escribe el código y tu nueva contraseña")
            Campo(vm.codigo, { vm.codigo = it }, "Código de 6 dígitos", teclado = KeyboardType.Number)
            Campo(vm.nueva, { vm.nueva = it }, "Nueva contraseña (mínimo 6)", esClave = true)
            Campo(vm.nueva2, { vm.nueva2 = it }, "Repite la nueva contraseña", esClave = true)
            MensajeError(vm.error)
            BotonPrincipal("CAMBIAR CONTRASEÑA", {
                vm.cambiarClave { mensaje ->
                    Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show()
                    onListo()
                }
            }, cargando = vm.cargando && vm.codigoEnviado, color = Rosa)
            TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al inicio de sesión", color = Violeta)
            }
        }
    }
}
