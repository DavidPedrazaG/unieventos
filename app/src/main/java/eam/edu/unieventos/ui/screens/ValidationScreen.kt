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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValidationScreen(
    onValidationSuccess: () -> Unit
) {
    val generatedCode by remember { mutableStateOf((100000..999999).random().toString()) }
    var code by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(59) }
    var validationError by remember { mutableStateOf(false) }
    val context = LocalContext.current


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
                text = "Tu código de validación: $generatedCode",
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )


            TextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(text = "Codigo de validación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine= true
            )

            Text(
                text = "VÁLIDO POR $timer SEGUNDOS",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Reenviar codigo?",
                color = Color(0xFF00BFFF),


                )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (code == generatedCode) {
                        setValidationStatus(context)
                        onValidationSuccess()
                    } else {
                        validationError = true
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
                    text = "Validar cuenta",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            if (validationError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Código incorrecto", color = Color.Red)

            }
        }
    }
}

fun validateCode(context: Context, inputCode: String): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val savedCode = sharedPreferences.getString("validation_code", "")
    return inputCode == savedCode
}


fun setValidationStatus(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("is_validated", true)
    editor.apply()
}