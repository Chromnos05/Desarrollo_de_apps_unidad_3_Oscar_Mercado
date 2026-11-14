package co.edu.unicartagena.musicapp.ui.navegacion

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.unicartagena.musicapp.MusicApp
import co.edu.unicartagena.musicapp.ui.auth.LoginScreen
import co.edu.unicartagena.musicapp.ui.auth.RecuperarScreen
import co.edu.unicartagena.musicapp.ui.auth.RegistroScreen
import co.edu.unicartagena.musicapp.ui.canciones.CancionFormScreen
import co.edu.unicartagena.musicapp.ui.canciones.CancionesScreen
import co.edu.unicartagena.musicapp.ui.menu.MenuScreen
import co.edu.unicartagena.musicapp.ui.perfil.PerfilScreen
import co.edu.unicartagena.musicapp.ui.reportes.ReporteCancionesScreen
import co.edu.unicartagena.musicapp.ui.reportes.ReporteUsuariosScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val RECUPERAR = "recuperar?email={email}"
    const val MENU = "menu"
    const val CANCIONES = "canciones"
    const val CANCION = "cancion/{id}"
    const val PERFIL = "perfil"
    const val REPORTE_CANCIONES = "reporte-canciones"
    const val REPORTE_USUARIOS = "reporte-usuarios"

    fun recuperar(email: String) = "recuperar?email=$email"
    fun cancion(id: Long?) = "cancion/${id ?: -1L}"
}

/**
 * Grafo de navegación de la app.
 * @param contadorAgitado aumenta cada vez que el acelerómetro detecta que se agitó el celular.
 * @param pedirHuella muestra el diálogo de huella y llama a su parámetro si la identidad se confirma.
 */
@Composable
fun MusicAppNavegacion(contadorAgitado: Int, pedirHuella: (onConfirmado: () -> Unit) -> Unit) {
    val nav = rememberNavController()
    val contexto = LocalContext.current
    val app = contexto.applicationContext as MusicApp

    fun irAlLogin(mensaje: String? = null) {
        mensaje?.let { Toast.makeText(contexto, it, Toast.LENGTH_LONG).show() }
        nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
    }
    val sesionExpirada = {
        app.auth.cerrarSesion()
        irAlLogin("Tu sesión expiró. Inicia sesión de nuevo.")
    }

    NavHost(navController = nav, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onEntrar = { nav.irAlMenu() },
                onRegistro = { nav.navigate(Rutas.REGISTRO) },
                onRecuperar = { email -> nav.navigate(Rutas.recuperar(email)) },
                onHuella = { pedirHuella { nav.irAlMenu() } },
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(onRegistrado = { nav.irAlMenu() }, onVolver = { nav.popBackStack() })
        }
        composable(
            Rutas.RECUPERAR,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" }),
        ) { entrada ->
            RecuperarScreen(
                emailInicial = entrada.arguments?.getString("email").orEmpty(),
                onListo = { nav.popBackStack() },
                onVolver = { nav.popBackStack() },
            )
        }
        composable(Rutas.MENU) {
            MenuScreen(
                onCanciones = { nav.navigate(Rutas.CANCIONES) },
                onPerfil = { nav.navigate(Rutas.PERFIL) },
                onReporteCanciones = { nav.navigate(Rutas.REPORTE_CANCIONES) },
                onReporteUsuarios = { nav.navigate(Rutas.REPORTE_USUARIOS) },
                onSalir = { irAlLogin() },
            )
        }
        composable(Rutas.CANCIONES) {
            CancionesScreen(
                refrescarPorSensor = contadorAgitado,
                onNueva = { nav.navigate(Rutas.cancion(null)) },
                onAbrir = { id -> nav.navigate(Rutas.cancion(id)) },
                onVolver = { nav.popBackStack() },
                onSesionExpirada = sesionExpirada,
            )
        }
        composable(Rutas.CANCION, arguments = listOf(navArgument("id") { type = NavType.LongType })) { entrada ->
            val id = entrada.arguments?.getLong("id")?.takeIf { it > 0 }
            CancionFormScreen(
                id = id,
                onListo = { nav.popBackStack() },
                onVolver = { nav.popBackStack() },
                onSesionExpirada = sesionExpirada,
            )
        }
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onVolver = { nav.popBackStack() },
                onCuentaEliminada = { irAlLogin() },
                onSesionExpirada = sesionExpirada,
            )
        }
        composable(Rutas.REPORTE_CANCIONES) {
            ReporteCancionesScreen(onVolver = { nav.popBackStack() }, onSesionExpirada = sesionExpirada)
        }
        composable(Rutas.REPORTE_USUARIOS) {
            ReporteUsuariosScreen(onVolver = { nav.popBackStack() }, onSesionExpirada = sesionExpirada)
        }
    }
}

private fun NavHostController.irAlMenu() {
    navigate(Rutas.MENU) { popUpTo(Rutas.LOGIN) { inclusive = true } }
}
