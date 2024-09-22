package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

@Composable
fun LocationRow(
    index: Int,
    locationName: String,
    onNameChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Campo de texto que muestra el número de la localidad
        OutlinedTextField(
            value = index.toString(),
            onValueChange = {},
            label = {
                Text(
                    text = "Localidad",
                    fontSize = 10.sp
                )
            },
            readOnly = true,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Campo de texto para el nombre de la localidad
        OutlinedTextField(
            value = locationName,
            onValueChange = onNameChange,
            label = { Text(text = "Nombre") },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Campo de texto para el precio de la localidad
        OutlinedTextField(
            value = price,
            onValueChange = onPriceChange,
            label = { Text(text = "Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(100.dp)
        )
    }
}
