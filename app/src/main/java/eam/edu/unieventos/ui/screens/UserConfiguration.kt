package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import eam.edu.unieventos.utils.SharedPreferenceUtils

@Composable
fun UserConfigurationScreen(

    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit

) {
    val context = LocalContext.current
    var client_id = SharedPreferenceUtils.getCurrenUser(context)
    var user = client_id?.let { ClientsViewModel(context).getByUserId(it.id) }
    var client = user as Client
    var name by remember { mutableStateOf(client.name) }
    var id by remember { mutableStateOf(client.idCard) }
    var email by remember { mutableStateOf(client.email ) }
    var phoneNumber by remember { mutableStateOf(client.phoneNumber ) }
    var password by remember { mutableStateOf(client.password ) }
    var address by remember { mutableStateOf(client.address ) }



    val clientsViewModel: ClientsViewModel = remember { ClientsViewModel(context) }

    Box(modifier = Modifier.fillMaxSize()) {

        IconButton(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color(0xFF6A0dad)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray, shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.usuario_login),
                    contentDescription = "Gestion Usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


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
                value = address, // Nuevo campo de dirección
                onValueChange = { address = it },
                label = { Text(text = "Dirección") },
                modifier = Modifier.fillMaxWidth()
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


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {

                        client.name = name
                        client.idCard = id
                        client.email = email
                        client.phoneNumber = phoneNumber
                        client.address = address
                        client.password = password
                        clientsViewModel.updateClient(client)
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0dad),
                        contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "GUARDAR CAMBIOS", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        client.isActive = true
                        clientsViewModel.deactivateClient(client)
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0dad),
                        contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "ELIMINAR CUENTA", fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}
