package co.edu.unicartagena.musicapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val RITMOS = listOf(
    "Rock", "Pop", "Salsa", "Vallenato", "Reggaeton", "Balada", "Merengue", "Bachata",
    "Cumbia", "Champeta", "Rap", "Electronica", "Jazz", "Otro",
)

/** Lista desplegable para elegir el ritmo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorRitmo(ritmo: String, onRitmo: (String) -> Unit, modifier: Modifier = Modifier) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }, modifier = modifier) {
        OutlinedTextField(
            value = ritmo,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ritmo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            RITMOS.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) }, onClick = { onRitmo(opcion); abierto = false })
            }
        }
    }
}
