package co.edu.unicartagena.musicapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicartagena.musicapp.ui.theme.DegradadoMarca
import co.edu.unicartagena.musicapp.ui.theme.Gris
import co.edu.unicartagena.musicapp.ui.theme.GrisCampo
import co.edu.unicartagena.musicapp.ui.theme.Violeta

/** Encabezado con el degradado de la marca, título, subtítulo y botón de volver opcional. */
@Composable
fun Encabezado(titulo: String, subtitulo: String? = null, onVolver: (() -> Unit)? = null,
               acciones: @Composable () -> Unit = {}) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(DegradadoMarca)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
            } else {
                Spacer(Modifier.size(12.dp))
            }
            Column(Modifier.weight(1f).padding(vertical = 6.dp)) {
                Text(titulo, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                if (subtitulo != null) {
                    Text(subtitulo, color = Color(0xFFEDE9FE), fontSize = 13.sp)
                }
            }
            acciones()
        }
    }
}

/** Tarjeta blanca redondeada. */
@Composable
fun Tarjeta(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = contenido)
    }
}

/** Campo de texto con el estilo de la app. */
@Composable
fun Campo(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false,
    habilitado: Boolean = true,
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else teclado),
        visualTransformation = if (esClave && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (esClave) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = GrisCampo,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Violeta,
        ),
    )
}

/** Botón principal; muestra un indicador mientras [cargando] es verdadero. */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    color: Color = Violeta,
    colorTexto: Color = Color.White,
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = colorTexto),
    ) {
        if (cargando) {
            CircularProgressIndicator(Modifier.size(22.dp), color = colorTexto, strokeWidth = 2.dp)
        } else {
            Text(texto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

/** Título pequeño de sección en mayúsculas. */
@Composable
fun Seccion(texto: String) {
    Text(texto.uppercase(), color = Violeta, fontSize = 12.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp))
}

/** Texto gris de ayuda. */
@Composable
fun Ayuda(texto: String, modifier: Modifier = Modifier) {
    Text(texto, color = Gris, fontSize = 13.sp, modifier = modifier)
}
