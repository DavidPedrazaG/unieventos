package eam.edu.unieventos.ui.screens


import ClientsViewModel
import eam.edu.unieventos.model.Client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

@Composable
fun RecoveryScreen(
    onNavigateBack: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") } // Nueva contraseña
    var temporalCode by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(59) } // Temporizador de ejemplo
    val context = LocalContext.current
    val clientViewModel: ClientsViewModel = remember { ClientsViewModel(context) }

    Box(modifier = Modifier
        .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RECUPERAR CONTRASEÑA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Correo Electrónico") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    // Lógica para validar el correo
                    val client = clientViewModel.validateEmail(email) // Cambia aquí si es necesario
                    if (client != null) {
                        temporalCode = (100000..999999).random().toString()
                        isEmailValid = true
                    } else {
                        // Manejar caso de correo no válido
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
                    text = "ENVIAR CÓDIGO",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            if (isEmailValid) {
                Text(
                    text = "TU CÓDIGO ES: $temporalCode",
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            TextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(text = "Ingresar código") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(text = "Ingresar contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Text(
                text = "VÁLIDO POR $timer SEGUNDOS",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    if (code == temporalCode) {
                        clientViewModel.updatePassword(email, newPassword) // Cambia aquí si es necesario
                        onNavigateBack()
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
                    text = "RECUPERAR CONTRASEÑA",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
