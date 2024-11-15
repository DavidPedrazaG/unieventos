package eam.edu.unieventos.ui.screens

import eam.edu.unieventos.ui.viewmodel.UsersViewModel
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Role

import eam.edu.unieventos.utils.SharedPreferenceUtils
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.stringResource
import eam.edu.unieventos.services.EmailService
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onNavigateToValidate: (String, String) -> Unit,
    onNavigateToHome: (String) -> Unit
) {
    val context = LocalContext.current
    Scaffold { padding ->
        LoginForm(
            padding = padding,
            context = context,
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToRecovery = onNavigateToRecovery,
            onNavigateToValidate = onNavigateToValidate,
            onNavigateToHome = onNavigateToHome
        )
    }
}

@Composable
fun LoginForm(
    padding: PaddingValues,
    context: Context,
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onNavigateToValidate: (String, String) -> Unit,
    onNavigateToHome: (String) -> Unit
) {
    val usersViewModel: UsersViewModel = remember { UsersViewModel(context) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val scope = rememberCoroutineScope()
    var clientsExist by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.usuario_login),
            contentDescription = "Login Image",
            modifier = Modifier.size(372.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = stringResource(id = R.string.emailLabel))},
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = stringResource(id = R.string.passwordLabel)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    scope.launch {
                        clientsExist = usersViewModel.checkIfClientExist()
                        if(clientsExist){
                            println("Clientes encontrados en la colección")
                        } else {
                            println("No hay clientes en la colección")
                        }
                        val user = usersViewModel.login(email, password)
                        if (user != null) {
                            val isValidated = user.isValidated
                            SharedPreferenceUtils.savePreference(context, user.id, user.role)

                            val generatedCode = (100000..999999).random().toString()

                            val emailService = EmailService()
                            emailService.sendEmail(
                                to = email,
                                subject = "Código de validación",
                                body = "Tu código de validación es: $generatedCode"
                            )

                            if (isValidated || user.role == "Admin") {
                                onNavigateToHome(user.role)
                            } else {
                                onNavigateToValidate(email, generatedCode)
                            }
                        } else {
                            loginError = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A0dad),
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(id = R.string.signIn))
            }

            Button(
                onClick = { onNavigateToRegister() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A0dad),
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(id = R.string.register))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.forgottenPassword),
            color = Color(0xFF00BFFF),
            modifier = Modifier.clickable {
                onNavigateToRecovery()
            }
        )

        if (loginError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Credenciales incorrectas", color = Color.Red)
        }
    }
}


    

