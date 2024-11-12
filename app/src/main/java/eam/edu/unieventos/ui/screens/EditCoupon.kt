package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.layout.*
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

    // Si el cupón no existe, mostrar un mensaje y cerrar la pantalla
    if (coupon == null) {
        Text(text = "Cupón no encontrado")
        return
    }

    // Estado para los campos editables
    var discountPercentage by remember { mutableStateOf(coupon?.discountPercentage ?: 0f) }
    var isActive by remember { mutableStateOf(coupon.isActive) }

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
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "${stringResource(id = R.string.editCoupon)}${coupon.code}", style = MaterialTheme.typography.titleLarge)

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar fecha de expiración (no editable si tiene evento)
        Text(text = "${stringResource(id = R.string.expiration_date)} ${coupon.expirationDate}")

        if (!hasEvent) {
            // Campo para cambiar la fecha (editable solo si no tiene evento)
            OutlinedTextField(
                value = coupon.expirationDate.toString(),
                onValueChange = { newValue ->
                    // Aquí podrías parsear y validar la nueva fecha
                },
                label = { Text(stringResource(id = R.string.expirationdateeditable)) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(text = stringResource(id = R.string.messageNoChangeDate))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para desactivar el cupón
        Button(
            onClick = { showDeactivateDialog = true }, // Activar el diálogo de confirmación
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.deactivate))
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar cambios
        Button(
            onClick = {
                // Guardar cambios en el porcentaje de descuento
                couponsViewModel.updateCoupon(coupon.copy(discountPercentage = discountPercentage, isActive = true))
                onClose() // Cerrar la pantalla después de guardar
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.saveChange))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cerrar la ventana
        Button(
            onClick = { onClose() },
            modifier = Modifier.fillMaxWidth()
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
