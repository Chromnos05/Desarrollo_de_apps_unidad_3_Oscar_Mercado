package co.edu.unicartagena.musicapp

import android.app.Application
import co.edu.unicartagena.musicapp.data.local.SessionStore
import co.edu.unicartagena.musicapp.data.remote.ApiClient
import co.edu.unicartagena.musicapp.data.repository.AuthRepository
import co.edu.unicartagena.musicapp.data.repository.CancionRepository

/** Crea una sola vez las dependencias compartidas (inyección de dependencias manual). */
class MusicApp : Application() {

    lateinit var auth: AuthRepository
        private set
    lateinit var canciones: CancionRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val session = SessionStore(this)
        val api = ApiClient.crear(session)
        auth = AuthRepository(api, session)
        canciones = CancionRepository(api)
    }
}
