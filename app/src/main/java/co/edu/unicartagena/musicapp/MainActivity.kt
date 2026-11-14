package co.edu.unicartagena.musicapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
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
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("MusicApp")
                }
            }
        }
    }
}
