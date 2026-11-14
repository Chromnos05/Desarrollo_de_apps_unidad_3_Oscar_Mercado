package co.edu.unicartagena.musicapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import co.edu.unicartagena.musicapp.ui.navegacion.MusicAppNavegacion
import co.edu.unicartagena.musicapp.ui.theme.MusicAppTheme

/**
 * Única actividad de la app. Hereda de FragmentActivity porque el diálogo de huella
 * (BiometricPrompt) necesita una FragmentActivity.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                MusicAppNavegacion(
                    contadorAgitado = 0,
                    pedirHuella = {
                        Toast.makeText(this, "La huella se habilita en la siguiente versión", Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }
}
