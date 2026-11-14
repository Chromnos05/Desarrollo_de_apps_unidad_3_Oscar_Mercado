package co.edu.unicartagena.musicapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import co.edu.unicartagena.musicapp.sensores.AutenticadorHuella
import co.edu.unicartagena.musicapp.sensores.DetectorAgitado
import co.edu.unicartagena.musicapp.ui.navegacion.MusicAppNavegacion
import co.edu.unicartagena.musicapp.ui.theme.MusicAppTheme

/**
 * Única actividad de la app. Hereda de FragmentActivity porque el diálogo de huella
 * (BiometricPrompt) necesita una FragmentActivity.
 *
 * Sensores:
 *  - Acelerómetro: cada vez que se agita el celular aumenta [contadorAgitado]; la pantalla
 *    de canciones lo observa y recarga la lista.
 *  - Huella: confirma la identidad para entrar con la sesión guardada.
 */
class MainActivity : FragmentActivity() {

    private var contadorAgitado by mutableIntStateOf(0)
    private lateinit var detector: DetectorAgitado
    private lateinit var huella: AutenticadorHuella

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detector = DetectorAgitado(this) { contadorAgitado++ }
        huella = AutenticadorHuella(this)

        setContent {
            MusicAppTheme {
                MusicAppNavegacion(
                    contadorAgitado = contadorAgitado,
                    pedirHuella = { alConfirmar ->
                        huella.pedir(alConfirmar) { mensaje ->
                            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        detector.iniciar()
    }

    override fun onPause() {
        super.onPause()
        detector.detener()
    }
}
