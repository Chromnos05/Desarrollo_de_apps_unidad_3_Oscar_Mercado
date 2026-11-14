package co.edu.unicartagena.musicapp.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Endpoints de la API REST de MusicApp (Spring Boot, unidad 2). */
interface MusicApiService {

    // ---------- Autenticación (públicas) ----------
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): SesionDto

    @POST("auth/registro")
    suspend fun registrar(@Body body: RegistroRequest): SesionDto

    @POST("auth/recuperar")
    suspend fun recuperar(@Body body: RecuperarRequest): MensajeDto

    @POST("auth/restablecer")
    suspend fun restablecer(@Body body: RestablecerRequest): MensajeDto

    // ---------- Usuario (requiere token) ----------
    @GET("usuarios/me")
    suspend fun miPerfil(): UsuarioDto

    @PUT("usuarios/me")
    suspend fun actualizarPerfil(@Body body: PerfilRequest): UsuarioDto

    @DELETE("usuarios/me")
    suspend fun eliminarCuenta(): MensajeDto

    @GET("usuarios/reportes/username")
    suspend fun reportePorUsername(@Query("valor") username: String): List<UsuarioDto>

    @GET("usuarios/reportes/nombre")
    suspend fun reportePorNombre(@Query("prefijo") prefijo: String): List<UsuarioDto>

    // ---------- Canciones (requiere token) ----------
    @GET("canciones")
    suspend fun listarCanciones(): List<CancionDto>

    @GET("canciones/{id}")
    suspend fun obtenerCancion(@Path("id") id: Long): CancionDto

    @POST("canciones")
    suspend fun crearCancion(@Body body: CancionDto): CancionDto

    @PUT("canciones/{id}")
    suspend fun actualizarCancion(@Path("id") id: Long, @Body body: CancionDto): CancionDto

    @DELETE("canciones/{id}")
    suspend fun eliminarCancion(@Path("id") id: Long): MensajeDto

    @GET("canciones/reportes/ritmo")
    suspend fun reportePorRitmo(@Query("ritmo") ritmo: String): List<CancionDto>

    @GET("canciones/reportes/fechas")
    suspend fun reportePorFechas(@Query("desde") desde: String, @Query("hasta") hasta: String): List<CancionDto>

    @GET("canciones/reportes/duracion")
    suspend fun reportePorDuracion(@Query("min") min: Int, @Query("max") max: Int): List<CancionDto>
}
