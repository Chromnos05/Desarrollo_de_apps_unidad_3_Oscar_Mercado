# MusicApp – Unidad 3: App nativa Android (Kotlin + Jetpack Compose)

Aplicación nativa **Android** para el **Ejercicio N° 20 – Canciones**, escrita en **Kotlin** con **Jetpack Compose**. Guarda sus datos en la **API REST** de la unidad 2 (Spring Boot con arquitectura hexagonal, desplegada en **Vercel**, base de datos **PostgreSQL en Supabase**) y usa **dos sensores**: huella digital y acelerómetro.

| Dato | Valor |
|---|---|
| Estudiante | 75022410035 – Oscar Mercado |
| Email institucional | omercador@unicartagena.edu.co |
| Ejercicio | 20 – Canciones |
| Actividad | Unidad 3 – Android Studio (Kotlin) con persistencia en API REST y sensores |
| API (backend en la nube) | https://musicapp-api-ten.vercel.app/api/health · [código](https://github.com/Chromnos05/Desarrollo_de_apps_unidad_2_Oscar_Mercado) |
| APK | [`apk/MusicApp.apk`](apk/MusicApp.apk) · también en la release [apk-latest](https://github.com/Chromnos05/Desarrollo_de_apps_unidad_3_Oscar_Mercado/releases/tag/apk-latest) |
| Video explicativo | _Pendiente_ |

---

## 1. Requerimientos y cómo se cumplen

| Requerimiento | Implementación |
|---|---|
| Base de datos gratuita en internet | PostgreSQL en Supabase, a través de la API REST |
| Backend desplegado en la nube | API Spring Boot en Vercel; cada merge a `main` se despliega solo |
| CRUD de las 2 tablas | **Canciones**: lista, crear, ver, actualizar, eliminar · **Usuario**: registro, Mi perfil (ver/actualizar), eliminar cuenta |
| 2 o más reportes por tabla | Canciones: por ritmo, rango de fechas y duración · Usuarios: por username y por nombre que empieza por… |
| Login | Correo **o** username y contraseña; la API devuelve un token JWT que la app envía en cada petición |
| Recordatorio de clave por email | *¿Olvidaste tu contraseña?* → la API envía un código de 6 dígitos al correo → nueva contraseña |
| Sensor 1 · Acelerómetro | En **Canciones**, agitar el celular recarga la lista (`sensores/DetectorAgitado.kt`) |
| Sensor 2 · Huella digital | **Entrar con huella** usando la sesión guardada, sin escribir la contraseña (`sensores/AutenticadorHuella.kt`) |

---

## 2. Arquitectura de la app (MVVM)

```
app/src/main/java/co/edu/unicartagena/musicapp
├── MainActivity.kt            Única actividad; registra el acelerómetro y el lector de huella
├── MusicApp.kt                Application: crea las dependencias (inyección manual)
├── data
│   ├── remote                 Retrofit: MusicApiService (endpoints), ApiModels (DTOs), ApiClient (token Bearer)
│   ├── local                  SessionStore: token JWT y datos de la sesión en el dispositivo
│   └── repository             AuthRepository, CancionRepository y Resultado (manejo de errores en español)
├── sensores                   DetectorAgitado (acelerómetro) y AutenticadorHuella (BiometricPrompt)
└── ui
    ├── theme                  Colores y formas de MusicApp (Material 3)
    ├── components             Encabezado, tarjetas, campos, selector de fecha y de ritmo
    ├── navegacion             Rutas y NavHost (Navigation Compose)
    ├── auth                   Login, registro y recuperación de clave (+ ViewModels)
    ├── menu                   Menú principal
    ├── canciones              Lista y formulario de canciones (+ ViewModels)
    ├── perfil                 Mi perfil
    └── reportes               Reportes de canciones y de usuarios
```

Cada pantalla tiene su **ViewModel**, que llama a un **repositorio**, que a su vez usa **Retrofit** para hablar con la API. Los errores de la API (`{"mensaje": "..."}`) se muestran tal cual en la pantalla. Si el token vence (HTTP 401), la app vuelve al login.

---

## 3. Instalar y probar el APK

1. Descarga [`apk/MusicApp.apk`](apk/MusicApp.apk) en un celular con **Android 8.0 o superior** y ábrelo. Si Android lo pide, permite *instalar apps de origen desconocido*.
2. **Regístrate** (nombre, username, correo y contraseña) o inicia sesión con una cuenta existente. Es la misma base de datos de la unidad 2.
3. En el menú:
   - **Canciones**: toca *Nueva*, completa los datos y guarda. Toca una canción para editarla o eliminarla. **Agita el celular** para actualizar la lista.
   - **Mi perfil**: edita el username o el nombre, o elimina tu cuenta.
   - **Reportes**: elige el parámetro y toca el botón del reporte.
4. **Huella**: toca *Salir* en el menú. En el login aparece **Entrar con huella digital**.
5. **Recuperar contraseña**: en el login toca *¿Olvidaste tu contraseña?*, envía el código, revisa tu correo y escribe el código y la nueva contraseña.

> La primera petición después de un rato sin uso puede tardar unos segundos mientras el servidor arranca.

---

## 4. Abrir y compilar el proyecto en Android Studio

**Requisitos:** Android Studio (Ladybug o más reciente), JDK 17 o superior (incluido en Android Studio) y Android SDK 35.

```bash
git clone https://github.com/Chromnos05/Desarrollo_de_apps_unidad_3_Oscar_Mercado.git
```

1. Android Studio → **Open** → elige la carpeta clonada y espera a que Gradle sincronice.
2. Conecta un celular con *depuración USB* activada (o usa un emulador) y presiona **Run ▶**.
3. Para generar el APK: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**. Desde consola:
   ```bash
   ./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
   # → app/build/outputs/apk/debug/app-debug.apk
   ```

La URL de la API se configura en `app/build.gradle.kts` (`API_URL`).

> La huella y el acelerómetro necesitan un celular real. En el emulador se pueden simular desde *Extended controls → Fingerprint / Virtual sensors*.

---

## 5. Integración continua

GitHub Actions (`.github/workflows/android.yml`) compila la app y ejecuta las pruebas unitarias en cada push. En la rama `main` además publica el APK en la release **apk-latest**.

Flujo de trabajo usado:
```bash
git checkout -b feature/nueva-funcionalidad
# ... cambios y commits ...
git checkout main
git merge --no-ff feature/nueva-funcionalidad
git push origin main
```

---

## 6. Tecnologías

Kotlin 2.1 · Jetpack Compose (Material 3) · Navigation Compose · ViewModel · Retrofit + Gson + OkHttp · AndroidX Biometric · SensorManager (acelerómetro) · Coroutines · JUnit · GitHub Actions. Backend: Spring Boot + Supabase + Vercel (unidad 2).
