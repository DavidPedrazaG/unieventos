package com.example.mobileappsproject.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.mobileappsproject.R



@Composable
fun LoginScreen(
    //En los parametros de la función debemos llamar a la función que creamos como una lambda
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onNavigateToValidate: () -> Unit
) {
    val context = LocalContext.current

    Scaffold { padding ->
        //Aqui llamamos a la función que tiene el contenido de la inferfaz y en los parametros que pide, le enviamos las interfaces que usaremos
        LoginForm(
            padding = padding,
            context = context,
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToRecovery = onNavigateToRecovery,
            onNavigateToValidate = onNavigateToValidate
        )

    }
}

@Composable
fun LoginForm(
    //En esta función tambien llamaremos a las funciones lambda que usaremos para redireccionarnos
    padding: PaddingValues,
    context: Context,
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onNavigateToValidate: () -> Unit
){
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var loginError by remember { mutableStateOf(false) }
    
    
    
    
        Column(
            modifier = Modifier.fillMaxSize(),
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
                label = { Text("Email") },
                singleLine= true
            )
            Spacer(modifier = Modifier.height(8.dp))
    
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine= true
            )
            Spacer(modifier = Modifier.height(16.dp))
    
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (email == "Pedro" && password == "12345") {
                            //En el lugar donde deseemos que se llame esta funcion,simplemente la llamamos
                            onNavigateToValidate()
                        } else {
                            loginError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A0dad),
                        contentColor = Color.White
                    )
                ) {
                    Text("Iniciar Sesión")
                }
    
                Button(
                    onClick = {
                        //En el lugar donde deseemos que se llame esta funcion,simplemente la llamamos
                        onNavigateToRegister()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A0dad),
                        contentColor = Color.White
                    )
                ) {
                    Text("Registrarse")
                }
            }
    
            Spacer(modifier = Modifier.height(16.dp))
    
            Text(
                text = "Olvidaste tu contraseña?",
                color = Color(0xFF00BFFF),
                modifier = Modifier.clickable {
                    //En el lugar donde deseemos que se llame esta funcion,simplemente la llamamos
                    onNavigateToRecovery()
                }
            )
    
    
            if (loginError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Credenciales incorrectas", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    

