package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLocation() {
    var numberOfLocations by remember { mutableStateOf(1) }
    var locationNames = remember { mutableStateListOf<String>().apply { add("") } }
    var locationPrices = remember { mutableStateListOf<String>().apply { add("") } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(text = "Localidades", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para seleccionar cuántas localidades se quieren
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (numberOfLocations > 1) {
                    numberOfLocations -= 1
                    locationNames.removeLastOrNull()
                    locationPrices.removeLastOrNull()
                }
            }) {
                Icon(imageVector = Icons.Rounded.Remove, contentDescription = "Decrement")
            }

            OutlinedTextField(
                value = numberOfLocations.toString(),
                onValueChange = {},
                label = { Text(text = "Cantidad") },
                readOnly = true,
                modifier = Modifier.width(100.dp)
            )

            IconButton(onClick = {
                numberOfLocations += 1
                locationNames.add("")
                locationPrices.add("")
            }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Increment")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Componente dinámico que se genera según el número de localidades seleccionadas
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 0 until numberOfLocations) {
                LocationRow(
                    index = i + 1,
                    locationName = locationNames[i],
                    onNameChange = { locationNames[i] = it },
                    price = locationPrices[i],
                    onPriceChange = { locationPrices[i] = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Botón para guardar localidades
        Button(onClick = { /* Acción para guardar localidades */ }) {
            Text("Guardar Localidades")
        }
    }
}
