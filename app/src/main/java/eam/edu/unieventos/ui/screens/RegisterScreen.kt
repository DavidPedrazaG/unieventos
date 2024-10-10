package eam.edu.unieventos.ui.screens

import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Role
import eam.edu.unieventos.model.User

import eam.edu.unieventos.model.Client

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {

    var name by remember { mutableStateOf("") }

    var idCard by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    val isActive = true
    val userAppConfigId = "default-config-id"
    val isValidated = false

    val context = LocalContext.current
    val clientViewModel: ClientsViewModel = remember { ClientsViewModel(context) }

    var showAlert by remember { mutableStateOf(false) }

    var existingClient: Client? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campos para la información del cliente
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = stringResource(id = R.string.name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )


            TextField(
                value = idCard,
                onValueChange = { idCard = it },
                label = { Text(text = stringResource(id = R.string.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(id = R.string.Email)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = stringResource(id = R.string.phoneNumber)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = stringResource(id = R.string.address)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(id = R.string.passwordLabel)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    val deactivatedClient = clientViewModel.getDeactivatedClientByEmail(email)
                    if (deactivatedClient != null) {
                        existingClient = deactivatedClient
                        showAlert = true
                    } else {
                        val id = clientViewModel.generateUserId()
                        val newClient = Client(
                            availableCoupons = emptyList(),
                            purchaseHistory = emptyList(),
                            friends = emptyList(),
                            notifications = emptyList(),
                            cartId = null,
                            id = id,
                            idCard = idCard,
                            name = name,
                            phoneNumber = phone,
                            address = address,
                            email = email,
                            password = password,
                            isActive = isActive,
                            role = "client",
                            userAppConfigId = userAppConfigId,
                            isValidated = isValidated
                        )
                        clientViewModel.createUser(newClient)
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A0dad),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.register),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            TextButton(onClick = {
                onNavigateBack()
            }) {
                Text(
                    text = stringResource(id = R.string.questionLogin),
                    color = Color.Cyan,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showAlert && existingClient != null) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Usuario existente") },
            text = { Text("Se ha encontrado un usuario con el mismo correo. ¿Deseas reactivar la cuenta o crear una nueva?") },
            confirmButton = {
                Button(
                    onClick = {
                        existingClient?.let {
                            it.isActive = true
                            clientViewModel.updateClient(it)
                        }
                        showAlert = false
                        onNavigateToLogin()
                    }
                ) {
                    Text("Reactivar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        val id = clientViewModel.generateUserId()
                        val newClient = Client(
                            availableCoupons = emptyList(),
                            purchaseHistory = emptyList(),
                            friends = emptyList(),
                            notifications = emptyList(),
                            cartId = null,
                            id = id,
                            idCard = idCard,
                            name = name,
                            phoneNumber = phone,
                            address = address,
                            email = email,
                            password = password,
                            isActive = isActive,
                            role = "client",
                            userAppConfigId = userAppConfigId,
                            isValidated = isValidated
                        )
                        clientViewModel.createUser(newClient)
                        showAlert = false
                        onNavigateToLogin()
                    }
                ) {
                    Text("Crear nueva")
                }
            }
        )
    }
}