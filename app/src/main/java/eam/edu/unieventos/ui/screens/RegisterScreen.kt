package eam.edu.unieventos.ui.screens

import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import eam.edu.unieventos.model.Role
import eam.edu.unieventos.model.User

import eam.edu.unieventos.model.Client

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    // Variables para almacenar los datos del cliente
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var idCard by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Puedes inicializar isActive y userAppConfigId aquí según tu lógica
    val isActive = true // O según sea necesario
    val userAppConfigId = "default-config-id" // O el ID correspondiente

    val context = LocalContext.current
    val clientViewModel: ClientsViewModel = remember { ClientsViewModel(context) }

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
                label = { Text(text = "Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = id,
                onValueChange = { id = it },
                label = { Text(text = "ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine= true
            )

            TextField(
                value = idCard,
                onValueChange = { idCard = it },
                label = { Text(text = "Cédula") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Correo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = "Número de teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = "Dirección") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Button(
                onClick = {

                    val client = Client(
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
                        userAppConfigId = userAppConfigId
                    )
                    clientViewModel.createUser(client)
                    onNavigateToLogin()
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
                    text = "REGISTRARSE",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            TextButton(onClick = {
                onNavigateBack()
            }) {
                Text(
                    text = "YA TIENES CUENTA? INICIA SESIÓN",
                    color = Color.Cyan,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}