package co.edu.unicartagena.musicapp.sensores

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * SENSOR 2 · Lector de huella (biometría).
 * Muestra el diálogo del sistema para confirmar la identidad con la huella, el rostro
 * o, si no hay biometría, con el PIN/patrón del celular.
 */
class AutenticadorHuella(private val actividad: FragmentActivity) {

    private val autenticadores = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

    fun disponible(): Boolean =
        BiometricManager.from(actividad).canAuthenticate(autenticadores) == BiometricManager.BIOMETRIC_SUCCESS

    fun pedir(alConfirmar: () -> Unit, alFallar: (String) -> Unit) {
        if (!disponible()) {
            alFallar("Este celular no tiene huella ni bloqueo de pantalla configurado")
            return
        }
        val prompt = BiometricPrompt(
            actividad,
            ContextCompat.getMainExecutor(actividad),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = alConfirmar()

                override fun onAuthenticationError(codigo: Int, mensaje: CharSequence) {
                    if (codigo != BiometricPrompt.ERROR_USER_CANCELED && codigo != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        alFallar(mensaje.toString())
                    }
                }
            },
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirma tu identidad")
            .setSubtitle("Entrar a MusicApp")
            .setDescription("Usa tu huella o el bloqueo de pantalla")
            .setAllowedAuthenticators(autenticadores)
            .build()
        prompt.authenticate(info)
    }
}
