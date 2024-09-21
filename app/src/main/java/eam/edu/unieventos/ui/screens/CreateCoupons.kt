package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCoupons() {
    var discountAmount by remember { mutableStateOf("") }
    var event by remember { mutableStateOf("") }
    var dateEnd by remember { mutableStateOf("") }
    var exposeEvent by remember { mutableStateOf(false) }
    var events = listOf("Event 1", "Event 2", "Event 3", "Event 4")
    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Crear Cupones", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = discountAmount,
            onValueChange = { discountAmount = it },
            label = { Text("Porcentaje del Cupón") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = exposeEvent,
            onExpandedChange = {exposeEvent = !exposeEvent},
            modifier = Modifier.width(223.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                value = event,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(text = "seleccione el evento")
                },
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = exposeEvent)}
            )
            ExposedDropdownMenu(expanded = exposeEvent,
                onDismissRequest = { exposeEvent=false  }
            ) {
                events.forEach{ item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            event= item
                            exposeEvent = false
                        }
                    )
                }


            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dateEnd,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(text = "Fecha Fin")
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expandedDate = true
                    }
                )
                {
                    Icon(
                        imageVector =Icons.Rounded.DateRange ,
                        contentDescription = "Icon Date"
                    )
                }
                

            },
            modifier = Modifier.width(190.dp)
        )

        if(expandedDate){
            DatePickerDialog(
                onDismissRequest = { expandedDate = false },
                confirmButton = { 
                    TextButton(
                        onClick = {
                            val selectedDay = datePickerState.selectedDateMillis
                            if(selectedDay != null){
                                val date1 = Date(selectedDay)
                                val formattedDate = SimpleDateFormat("MMM ddd, yyyy", Locale.getDefault()).format(date1)
                                dateEnd = formattedDate
                            }
                            expandedDate = false
                        }
                    )
                    {
                        Text(text = "OK")
                        
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { expandedDate = false
                        }
                    ) {
                        Text(text = "Cancelar")
                    }
                }

            )
            {
                DatePicker(state = datePickerState)
                
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
        }) {
            Text("Crear Cupón")
        }


    }
}
