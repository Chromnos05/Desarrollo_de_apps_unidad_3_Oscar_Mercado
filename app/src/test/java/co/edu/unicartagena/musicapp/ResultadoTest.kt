package co.edu.unicartagena.musicapp

import co.edu.unicartagena.musicapp.data.repository.Resultado
import co.edu.unicartagena.musicapp.data.repository.llamarApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ResultadoTest {

    @Test
    fun llamadaExitosaDevuelveLosDatos() = runBlocking {
        val r = llamarApi { 42 }
        assertEquals(Resultado.Exito(42), r)
    }

    @Test
    fun errorDeRedSeConvierteEnMensajeEnEspanol() = runBlocking {
        val r = llamarApi<Int> { throw IOException("sin red") }
        assertTrue(r is Resultado.Error)
        assertTrue((r as Resultado.Error).mensaje.contains("conexión"))
    }

    @Test
    fun soloEl401IndicaSesionExpirada() {
        assertTrue(Resultado.Error("x", 401).sesionExpirada)
        assertTrue(!Resultado.Error("x", 400).sesionExpirada)
    }
}
