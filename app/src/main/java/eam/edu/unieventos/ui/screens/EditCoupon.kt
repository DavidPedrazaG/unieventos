package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.ui.viewmodel.CouponsViewModel
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.ui.viewmodel.LocationsViewModel
import eam.edu.unieventos.ui.viewmodel.UsersViewModel
import androidx.compose.ui.res.stringResource
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Event
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCoupon(

    couponCode: String,
    onClose: () -> Unit,
    onNavegateToCoupons: () -> Unit,
) {
    val context = LocalContext.current

    val couponsViewModel: CouponsViewModel = remember { CouponsViewModel() }
    val usersViewModel: UsersViewModel = remember { UsersViewModel(context) }


    // Obtener el cupón por su código
    var coupon by remember { mutableStateOf(Coupon())}

    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()

    // Si el cupón no existe, mostrar un mensaje y cerrar la pantalla
    if (coupon == null) {
        Text(text = "Cupón no encontrado")
        return
    }

    // Estado para los campos editables
    var discountPercentage by remember { mutableStateOf(coupon?.discountPercentage ?: 0f) }
    var isActive by remember { mutableStateOf(coupon.isActive) }
    var expirationDate by remember { mutableStateOf(coupon?.expirationDate) }


    // Determinar si el cupón tiene evento
    val hasEvent = coupon.eventCode != null
    var showDeactivateDialog by remember { mutableStateOf(false) }
    LaunchedEffect(couponCode) {
        coupon = couponsViewModel.getCouponByCode(couponCode)!!
        discountPercentage = coupon?.discountPercentage ?: 0.0f


    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(300.dp))
        Text(text = stringResource(id = R.string.editCoupon), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el código del cupón
        Text(text = "${stringResource(id = R.string.code)} ${coupon.code}")

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para el porcentaje de descuento (editable)
        OutlinedTextField(
            value = discountPercentage.toString(),
            onValueChange = { newValue ->
                discountPercentage = newValue.toFloatOrNull() ?: 0f
            },
            label = { Text(stringResource(id = R.string.discount_percentage)) },
            modifier = Modifier.fillMaxWidth(0.42f)
        )

        Spacer(modifier = Modifier.height(8.dp))


        if (!hasEvent) {

            // Fecha del evento
            OutlinedTextField(
                value = expirationDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        onClick = { expandedDate = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Icon Date"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.39f)
            )

            if (expandedDate) {
                DatePickerDialog(
                    onDismissRequest = { expandedDate = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDay = datePickerState.selectedDateMillis
                                if (selectedDay != null) {
                                    // Convertir a Calendar para manipular fácilmente
                                    val calendar = Calendar.getInstance()
                                    calendar.timeInMillis = selectedDay
                                    // Añadir un día
                                    calendar.add(Calendar.DAY_OF_MONTH, 0)
                                    // Asignar la nueva fecha
                                     expirationDate  = calendar.time
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

            Spacer(modifier = Modifier.height(16.dp))

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para desactivar el cupón
        Button(
            onClick = { showDeactivateDialog = true }, // Activar el diálogo de confirmación
            modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            Text(text = stringResource(id = R.string.deactivate))
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar cambios
        Button(
            onClick = {
                // Guardar cambios en el porcentaje de descuento
                expirationDate?.let { coupon.copy(discountPercentage = discountPercentage, expirationDate = it, isActive = true) }
                    ?.let { couponsViewModel.updateCoupon(it) }
                onClose() // Cerrar la pantalla después de guardar
            },
            modifier = Modifier.fillMaxWidth(0.34f)
        ) {
            Text(text = stringResource(id = R.string.saveChange))
       }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cerrar la ventana
        Button(
            onClick = { onClose() },
            modifier = Modifier.fillMaxWidth(0.25f)
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }

        if (showDeactivateDialog) {
            AlertDialog(
                onDismissRequest = { showDeactivateDialog = false },
                title = { Text(text = stringResource(id = R.string.confirm_deactivation)) },
                text = { Text(text = stringResource(id = R.string.deactivate_confirmation)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            couponsViewModel.deactivateCoupon(coupon)
                            showDeactivateDialog = false
                            onClose()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeactivateDialog = false }
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}
