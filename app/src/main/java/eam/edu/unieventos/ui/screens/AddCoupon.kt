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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.ui.viewmodel.CouponsViewModel
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCoupon(onBack: () -> Unit) {
    val context = LocalContext.current
    val couponViewModel: CouponsViewModel = remember { CouponsViewModel() }
    val eventViewModel: EventsViewModel = remember { EventsViewModel() }

    // Estado para los campos del formulario
    var code by remember { mutableStateOf("") }
    var discountPercentage by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf<Date?>(null) }
    var eventCode by remember { mutableStateOf("") }
    var isCouponByEvent by remember { mutableStateOf(true) } // true: Para Evento, false: Por Fecha
    var isActive by remember { mutableStateOf(true) }
    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()

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
                text = stringResource(id = R.string.create_coupon),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo para el porcentaje de descuento
            TextField(
                value = discountPercentage,
                onValueChange = { discountPercentage = it },
                label = { Text(text = stringResource(id = R.string.discount_percentage)) },
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
                Text(text = stringResource(id = R.string.coupon_type))
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
                    Text(text = stringResource(id = R.string.for_event))
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
                    Text(text = stringResource(id = R.string.by_date))
                }
            }

            // Campo para el código del evento (opcional)
            // Este campo solo será visible si isCouponByEvent es true
            if (isCouponByEvent) {
                TextField(
                    value = eventCode,
                    onValueChange = { eventCode = it },
                    label = { Text(text = stringResource(id = R.string.event_code)) },
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
                    placeholder = { Text(text = stringResource(id = R.string.expiration_date)) },
                    trailingIcon = {
                        IconButton(onClick = { expandedDate = true }) {
                            Icon(
                                imageVector = Icons.Rounded.DateRange,
                                contentDescription = stringResource(id = R.string.date_icon)
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
                                Text(text = stringResource(id = R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { expandedDate = false }) {
                                Text(text = stringResource(id = R.string.cancel))
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
                    if (discountPercentage.isEmpty()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.enter_discount_percentage),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (isCouponByEvent) {
                        if (eventCode.isEmpty()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.enter_event_code),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        scope.launch {
                            val event = eventViewModel.getEventByCode(eventCode)

                            if (event != null) {
                                expirationDate = event.dateEvent
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.event_not_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }

                            val newCoupon = Coupon(
                                id = "",
                                code = couponViewModel.generateCouponCode(),
                                discountPercentage = discountPercentage.toFloat(),
                                expirationDate = expirationDate!!,
                                eventCode = if (eventCode.isNotEmpty()) eventCode else null,
                                type = 2,
                                isActive = isActive
                            )
                            couponViewModel.createCoupon(newCoupon)
                            Toast.makeText(context, context.getString(R.string.coupon_created), Toast.LENGTH_SHORT).show()

                            code = ""
                            discountPercentage = ""
                            expirationDate = null
                            eventCode = ""
                            isCouponByEvent = true
                            isActive = true
                        }
                    } else {
                        if (expirationDate == null) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.select_expiration_date),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val newCoupon = Coupon(
                            id = "",
                            code = couponViewModel.generateCouponCode(),
                            discountPercentage = discountPercentage.toFloat(),
                            expirationDate = expirationDate!!,
                            eventCode = if (eventCode.isNotEmpty()) eventCode else null,
                            type = 2,
                            isActive = isActive
                        )
                        couponViewModel.createCoupon(newCoupon)
                        Toast.makeText(context, context.getString(R.string.coupon_created), Toast.LENGTH_SHORT).show()

                        code = ""
                        discountPercentage = ""
                        expirationDate = null
                        eventCode = ""
                        isCouponByEvent = true
                        isActive = true
                    }
                },
                modifier = Modifier.fillMaxWidth(0.4f)
            ) {
                Text(text = stringResource(id = R.string.create_coupon))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver atrás
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth(0.4f)
            ) {
                Text(text = stringResource(id = R.string.back))
            }
        }
    }
}
