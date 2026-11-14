package co.edu.unicartagena.musicapp.sensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * SENSOR 1 · Acelerómetro.
 * Detecta cuando el usuario agita el celular: calcula la fuerza G de la aceleración y,
 * si supera [umbralG], avisa con [alAgitar]. Espera [pausaMs] entre avisos para no repetir.
 */
class DetectorAgitado(
    context: Context,
    private val umbralG: Float = 2.7f,
    private val pausaMs: Long = 3000,
    private val alAgitar: () -> Unit,
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val acelerometro: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var ultimoAviso = 0L

    val disponible: Boolean get() = acelerometro != null

    fun iniciar() {
        acelerometro?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun detener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(evento: SensorEvent) {
        val (x, y, z) = Triple(evento.values[0], evento.values[1], evento.values[2])
        val fuerzaG = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
        val ahora = System.currentTimeMillis()
        if (fuerzaG > umbralG && ahora - ultimoAviso > pausaMs) {
            ultimoAviso = ahora
            alAgitar()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, precision: Int) = Unit
}
