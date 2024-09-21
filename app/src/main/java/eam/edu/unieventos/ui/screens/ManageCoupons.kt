package eam.edu.unieventos.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCoupons() {
    var couponCode by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf("") }
    var event by remember { mutableStateOf("") }
    var dateEnd by remember { mutableStateOf("") }
    var exposeEvent by remember { mutableStateOf(false) }
    var events = listOf("Event 1", "Event 2", "Event 3", "Event 4")
    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Gestionar Cupones", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de código del cupón
        OutlinedTextField(
            value = couponCode,
            onValueChange = {
                if (it.length <= 150) {
                    couponCode = it // Limitar a 150 caracteres
                }
            },
            label = { Text("Código del Cupón") },
            modifier = Modifier.width(250.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de porcentaje del cupón
        OutlinedTextField(
            value = discountAmount,
            onValueChange = { discountAmount = it },
            label = { Text("Porcentaje del Cupón") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de selección de evento asociado
        ExposedDropdownMenuBox(
            expanded = exposeEvent,
            onExpandedChange = { exposeEvent = !exposeEvent },
            modifier = Modifier.width(223.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                value = event,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(text = "Seleccione el evento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = exposeEvent) }
            )
            ExposedDropdownMenu(
                expanded = exposeEvent,
                onDismissRequest = { exposeEvent = false }
            ) {
                events.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            event = item
                            exposeEvent = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de fecha de expiración
        OutlinedTextField(
            value = dateEnd,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(text = "Fecha Fin") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expandedDate = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = "Icon Date"
                    )
                }
            },
            modifier = Modifier.width(190.dp)
        )

        if (expandedDate) {
            DatePickerDialog(
                onDismissRequest = { expandedDate = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDay = datePickerState.selectedDateMillis
                            if (selectedDay != null) {
                                val date1 = Date(selectedDay)
                                val formattedDate = SimpleDateFormat("MMM ddd, yyyy", Locale.getDefault()).format(date1)
                                dateEnd = formattedDate
                            }
                            expandedDate = false
                        }
                    ) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { expandedDate = false }
                    ) {
                        Text(text = "Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botones para eliminar, editar y buscar
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                // Lógica para eliminar el cupón
            }) {
                Text("Eliminar")
            }

            Button(onClick = {
                // Lógica para editar el cupón
            }) {
                Text("Editar")
            }

            Button(onClick = {
                // Lógica para buscar el cupón
            }) {
                Text("Buscar")
            }
        }
    }
}


