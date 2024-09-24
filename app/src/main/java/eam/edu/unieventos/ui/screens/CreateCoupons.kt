package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eam.edu.unieventos.R
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCoupons() {

    var discountAmount by remember { mutableStateOf("") }
    var event by remember { mutableStateOf("") }
    var dateEnd by remember { mutableStateOf("") }
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
        Text(text = stringResource(id = R.string.createCouponsTitle), style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = discountAmount,
            onValueChange = { discountAmount = it },
            label = { Text(stringResource(id = R.string.porcentageCoupon)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            value = event,
            onValeChange ={
                event = it
            } ,
            items = events
        )


        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dateEnd,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(text = stringResource(id = R.string.dateEnd))
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
                        Text(text = stringResource(id = R.string.ok))
                        
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { expandedDate = false
                        }
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
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
            Text(stringResource(id = R.string.createCoupon))
        }


    }
}
