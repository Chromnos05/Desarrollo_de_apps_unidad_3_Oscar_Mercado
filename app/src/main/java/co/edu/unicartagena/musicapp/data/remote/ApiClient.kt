package co.edu.unicartagena.musicapp.data.remote

import co.edu.unicartagena.musicapp.BuildConfig
import co.edu.unicartagena.musicapp.data.local.SessionStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Construye el cliente Retrofit. Agrega el token "Bearer" a cada petición si hay sesión. */
object ApiClient {

    fun crear(session: SessionStore): MusicApiService {
        val autenticacion = Interceptor { chain ->
            val token = session.token
            val peticion = if (token.isNullOrBlank()) {
                chain.request()
            } else {
                chain.request().newBuilder().header("Authorization", "Bearer $token").build()
            }
            chain.proceed(peticion)
        }
        val log = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        // Tiempos amplios: el servidor puede tardar unos segundos en "despertar" (arranque en frío).
        val http = OkHttpClient.Builder()
            .addInterceptor(autenticacion)
            .addInterceptor(log)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApiService::class.java)
    }
}
