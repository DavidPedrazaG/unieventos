package eam.edu.unieventos.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import eam.edu.unieventos.ui.viewmodel.UsersViewModel
import kotlinx.coroutines.launch

@Composable
fun ValidationScreen(
    email: String,
    onValidationSuccess: () -> Unit
) {

    var generatedCode by remember { mutableStateOf((100000..999999).random().toString()) }
    var code by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(60) }
    var validationError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clientsViewModel: ClientsViewModel = remember { ClientsViewModel(context) }
    val scope = rememberCoroutineScope()


    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            kotlinx.coroutines.delay(1000L)
            timer -= 1
        } else {

            generatedCode = (100000..999999).random().toString()
            timer = 60
        }
    }

    Box(modifier = Modifier
        .fillMaxSize(), contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "${stringResource(id = R.string.validateCode)} $generatedCode",
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )


            TextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(text = stringResource(id = R.string.validateCode)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine= true
            )

            Text(
                text = stringResource(id = R.string.valid_for_seconds, timer),
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.resendCode),
                color = Color(0xFF00BFFF),


                )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (code == generatedCode) {

                            val client = clientsViewModel.getUserByEmail(email)
                            if (client is Client) {

                                clientsViewModel.validateStatus(client)
                                onValidationSuccess()
                            } else {
                                println("Usuario no es válido o no existe.")
                            }
                        } else {
                            validationError = true
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
                    text = stringResource(id = R.string.validateAccount),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            if (validationError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.wrongCode), color = Color.Red)

            }
        }
    }


}




