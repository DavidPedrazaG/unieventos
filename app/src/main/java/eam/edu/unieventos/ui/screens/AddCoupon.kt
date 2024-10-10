package eam.edu.unieventos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.ui.viewmodel.CouponsViewModel
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCoupon(onBack: () -> Unit) {
    val context = LocalContext.current
    val couponViewModel: CouponsViewModel = remember { CouponsViewModel(context) }
    val eventViewModel: EventsViewModel = remember { EventsViewModel(context) }

    // Estado para los campos del formulario
    var code by remember { mutableStateOf("") }
    var discountPercentage by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf<Date?>(null) }
    var eventCode by remember { mutableStateOf("") }
    var isCouponByEvent by remember { mutableStateOf(true) } // true: Para Evento, false: Por Fecha
    var isActive by remember { mutableStateOf(true) }
    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()

    // Scroll para el contenido
    val scrollState = rememberScrollState()

    Scaffold(
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Crear Cupon",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo para el porcentaje de descuento
            TextField(
                value = discountPercentage,
                onValueChange = { discountPercentage = it },
                label = { Text(text = "Porcentaje de Descuento (%)") },
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(vertical = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Tipo de cupón
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Tipo de Cupón:")
                Spacer(modifier = Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isCouponByEvent,
                        onClick = {
                            isCouponByEvent = true
                            eventCode =
                                ""  // Limpiar el código de evento al cambiar a "Para Evento"
                            expirationDate = null // Limpiar la fecha al cambiar a "Para Evento"
                        }
                    )
                    Text(text = "Para Evento")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !isCouponByEvent,
                        onClick = {
                            isCouponByEvent = false
                            eventCode = ""  // Limpiar el código de evento al cambiar a "Por Fecha"
                            expirationDate = null // Limpiar la fecha al cambiar a "Por Fecha"
                        }
                    )
                    Text(text = "Por Fecha")
                }
            }

            // Campo para el código del evento (opcional)
            // Este campo solo será visible si isCouponByEvent es true
            if (isCouponByEvent) {
                TextField(
                    value = eventCode,
                    onValueChange = { eventCode = it },
                    label = { Text(text = "Código del Evento ") },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 8.dp),
                    singleLine = true
                )
            }

            // Campo para seleccionar la fecha de expiración
            // Este campo solo será visible si isCouponByEvent es false
            if (!isCouponByEvent) {
                OutlinedTextField(
                    value = expirationDate?.let {
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(it)
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(text = "Fecha de Expiración") },
                    trailingIcon = {
                        IconButton(onClick = { expandedDate = true }) {
                            Icon(
                                imageVector = Icons.Rounded.DateRange,
                                contentDescription = "Icono de Fecha"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Date Picker para la fecha de expiración
                if (expandedDate) {
                    DatePickerDialog(
                        onDismissRequest = { expandedDate = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val selectedDate = datePickerState.selectedDateMillis
                                    if (selectedDate != null) {
                                        expirationDate = Date(selectedDate)
                                    }
                                    expandedDate = false
                                }
                            ) {
                                Text(text = "OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { expandedDate = false }) {
                                Text(text = "Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar el cupón
            Button(
                onClick = {
                    // Lógica de validación


                    if (discountPercentage.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Por favor, ingresa el porcentaje de descuento",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (isCouponByEvent) {
                        if (eventCode.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Por favor, ingresa el código del evento",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        // Lógica para buscar el evento y verificar si está activo
                        val event = eventViewModel.getEventByCode(eventCode)
                        if (event != null) {
                            // Si el evento está activo, asignar la fecha del evento al cupón
                            expirationDate = event.dateEvent // Asignar la fecha del evento
                        } else {
                            Toast.makeText(
                                context,
                                "No se encontró un evento activo con ese código",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                    } else {
                        if (expirationDate == null) {
                            Toast.makeText(
                                context,
                                "Por favor, selecciona una fecha de expiración",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                    }

                    // Crear el nuevo cupón
                    val newCoupon = Coupon(
                        id = couponViewModel.generateCouponId(),
                        code = couponViewModel.generateCouponCode(),
                        discountPercentage = discountPercentage.toFloat(),
                        expirationDate = expirationDate!!,
                        eventCode = if (eventCode.isNotEmpty()) eventCode else null,
                        type = 2,
                        isActive = isActive
                    )
                    couponViewModel.createCoupon(newCoupon) // Guardar el cupón en el ViewModel
                    Toast.makeText(context, "Cupón creado correctamente", Toast.LENGTH_SHORT).show()

                    // Limpiar el formulario
                    code = ""
                    discountPercentage = ""
                    expirationDate = null
                    eventCode = ""
                    isCouponByEvent = true // Volver a la opción predeterminada
                    isActive = true
                    couponViewModel.printCoupons()
                },
                modifier = Modifier.fillMaxWidth(0.4f)
            ) {
                Text(text = "Crear Cupón")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver atrás
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth(0.4f)
            ) {
                Text(text = "Volver Atrás")
            }
        }
    }
}
