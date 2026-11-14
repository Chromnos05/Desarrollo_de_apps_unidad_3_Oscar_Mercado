package co.edu.unicartagena.musicapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unicartagena.musicapp.MusicApp

/** Crea un ViewModel pasándole las dependencias de [MusicApp]. */
@Composable
inline fun <reified VM : ViewModel> appViewModel(crossinline crear: (MusicApp) -> VM): VM {
    val app = LocalContext.current.applicationContext as MusicApp
    return viewModel { crear(app) }
}
