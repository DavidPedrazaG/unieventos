package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import eam.edu.unieventos.R
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@Composable
fun UserConfigurationScreen(
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),  // Llenar todo el espacio disponible
        verticalArrangement = Arrangement.Center, // Centrar verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
    ) {
        // Imagen de perfil circular
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.LightGray, shape = CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.usuario_login), // Coloca aquí la imagen de perfil
                contentDescription = "Gestion Usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos de texto para los datos del usuario
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(text = "Cedula") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Correo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(text = "Numero de telefono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de Guardar Cambios y Eliminar Cuenta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Guardar cambios */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0dad),
                    contentColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "GUARDAR CAMBIOS", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { /* Eliminar cuenta */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0dad),
                    contentColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "ELIMINAR CUENTA", fontSize = 14.sp, color = Color.White)
            }
        }
    }
}
