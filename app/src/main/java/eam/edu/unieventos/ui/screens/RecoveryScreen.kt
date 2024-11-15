package eam.edu.unieventos.ui.screens


import android.content.Context
import android.content.SharedPreferences
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
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
import androidx.compose.ui.res.stringResource
import eam.edu.unieventos.R
import kotlinx.coroutines.launch

@Composable
fun RecoveryScreen(
    email: String,
    onNavigateBack: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var temporalCode by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(60) }
    val context = LocalContext.current
    val clientViewModel: ClientsViewModel = remember { ClientsViewModel(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            kotlinx.coroutines.delay(1000L)
            timer -= 1
        } else {

            temporalCode = (100000..999999).random().toString()
            timer = 60
        }
    }

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
                text = stringResource(id = R.string.recoveryPassword),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
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

            Button(
                onClick = {

                    val client = clientViewModel.validateEmail(email){ client ->
                    if (client != null) {
                        temporalCode = (100000..999999).random().toString()
                        isEmailValid = true
                    } else {

                        }
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
                    text = stringResource(id = R.string.sendCode),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            if (isEmailValid) {
                Text(
                    text = "${stringResource(id = R.string.labelCodeRule)} $temporalCode",
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            TextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(text = stringResource(id = R.string.getIntoCode)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(text = stringResource(id = R.string.getIntoPassword)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Text(
                text = stringResource(id = R.string.valid_for_seconds, timer),
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    scope.launch{
                        if (code == temporalCode) {

                            updatePasswordClient(context,email, newPassword)
                            onNavigateBack()
                        }
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
                    text = stringResource(id = R.string.recoveryPassword),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

suspend fun updatePasswordClient(context: Context, email:String, newPassword: String) {
    val clientsViewModel: ClientsViewModel =  ClientsViewModel(context)
    val user = clientsViewModel.getUserByEmail(email)


    if (user is Client) {
        user.password = newPassword
        clientsViewModel.updateClient(user)
    } else {

        println("El usuario no es un cliente")
    }
}
