package eam.edu.unieventos.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R

@Composable
fun RecoveryScreen() {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") } // Nueva contraseña
    var timer by remember { mutableStateOf(59) } // Temporizador de ejemplo


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
                text = stringResource(id = R.string.recoveryPassword),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )


            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(id = R.string.emailLabel)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine= true
            )


            Button(
                onClick = {  },
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


            Text(
                text = stringResource(id = R.string.labelCodeRule),
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )


            TextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(text = stringResource(id = R.string.getIntoCode)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine= true
            )


            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(text = stringResource(id = R.string.getIntoPassword)) },
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


            Button(
                onClick = {  },
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
