package eam.edu.unieventos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Location
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.ui.viewmodel.LocationsViewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEvent() {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var poster by remember { mutableStateOf("") }
    var locationImage by remember { mutableStateOf("") }
    var dateEvent by remember { mutableStateOf<Date?>(null) }
    val isActive = true

    // Variables para localidades dinámicas
    var numberOfLocations by remember { mutableStateOf(1) }
    var locationNames = remember { mutableStateListOf<String>().apply { add("") } }
    var locationPrices = remember { mutableStateListOf<String>().apply { add("") } }
    var locationMaxCapacity = remember { mutableStateListOf<String>().apply { add("") } }

    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()

    val context = LocalContext.current
    val eventViewModel: EventsViewModel = remember { EventsViewModel(context) }
    val locationViewModel: LocationsViewModel = remember { LocationsViewModel(context) }

    // Scroll state para permitir desplazamiento
    val scrollState = rememberScrollState()

    //para los laction vatiables
    var namaLocation by remember { mutableStateOf("") }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Agregamos scroll a la columna
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState), // Añade el scroll aquí
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campos para la información del evento
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Nombre del Evento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = "Sitio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(text = "Ciudad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = type,
                onValueChange = { type = it },
                label = { Text(text = "Tipo de Evento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = poster,
                onValueChange = { poster = it },
                label = { Text(text = "URL del Póster") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = locationImage,
                onValueChange = { locationImage = it },
                label = { Text(text = "URL de la Imagen de Ubicación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            // Fecha del evento
            OutlinedTextField(
                value = dateEvent?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(text = stringResource(id = R.string.dateEnd)) },
                trailingIcon = {
                    IconButton(
                        onClick = { expandedDate = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Icon Date"
                        )
                    }
                },
                modifier = Modifier.width(190.dp)
            )

            if (expandedDate) {
                DatePickerDialog(
                    onDismissRequest = { expandedDate = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDay = datePickerState.selectedDateMillis
                                if (selectedDay != null) {
                                    dateEvent = Date(selectedDay)
                                }
                                expandedDate = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { expandedDate = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

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
                        locationMaxCapacity.removeLastOrNull()
                    }
                }) {
                    Icon(imageVector = Icons.Rounded.Remove, contentDescription = "Decrement")
                }

                OutlinedTextField(
                    value = numberOfLocations.toString(),
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.labelCounts)) },
                    readOnly = true,
                    modifier = Modifier.width(100.dp)
                )

                IconButton(onClick = {
                    numberOfLocations += 1
                    locationNames.add("")
                    locationPrices.add("")
                    locationMaxCapacity.add("")
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
                        onPriceChange = { locationPrices[i] = it },
                        maxCapacity = locationMaxCapacity[i],
                        onMaxChange = { locationMaxCapacity[i] = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Botón para guardar el evento
            Button(onClick = {
                    //locationViewModel.printLocations()
                //eventViewModel.logAllEvents()
                val eventCode = eventViewModel.generateRandomCode(6);
                val locations = mutableListOf<Location>()

                for (i in 0 until numberOfLocations) {
                    if (locationNames[i].isNotEmpty() && locationPrices[i].isNotEmpty() && locationMaxCapacity[i].isNotEmpty()) {
                        // Agregar la localidad a la lista solo si todos los campos no están vacíos
                        val location = Location(
                            id = eventViewModel.generateRandomCode(5), // Generar un ID único para la ubicación
                            name = locationNames[i],
                            price = locationPrices[i].toFloat(),
                            eventCode = eventCode,
                            maxCapacity = locationMaxCapacity[i].toInt(),
                            ticketsSold = 0,
                            isActive = isActive

                        )
                        locations.add(location)
                    }
                }
                val newEvent = dateEvent?.let {
                    Event(
                        id = eventViewModel.generateRandomCode(7),
                        code = eventCode ,
                        name = name,
                        place = address,
                        city = city,
                        description = description,
                        type = type,
                        poster = poster,
                        locationImage = locationImage,
                        locations = locations.map { it.id },
                        dateEvent = it,
                        isActive = isActive
                    )
                }

                if (newEvent != null) {
                    //eventViewModel.logAllEvents()
                    eventViewModel.createEvent(newEvent) // Guardar el evento en el ViewModel
                    Toast.makeText(context, "Evento guardado correctamente", Toast.LENGTH_SHORT).show()


                    locations.forEach { location ->
                        locationViewModel.createLocation(location) // Asegúrate de tener este método en tu ViewModel
                    }


                    // Limpiar el formulario
                    name = ""
                    address = ""
                    city = ""
                    description = ""
                    type = ""
                    poster = ""
                    locationImage = ""
                    dateEvent = null

                    numberOfLocations = 1
                    locationNames.clear()
                    locationPrices.clear()
                    locationMaxCapacity.clear()
                    locationNames.add("")
                    locationPrices.add("")
                    locationMaxCapacity.add("")

                    //eventViewModel.logAllEvents()
                } else {
                    Toast.makeText(context, "Por favor selecciona una fecha", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(stringResource(id = R.string.labelCreateLocation))
            }
        }
    }
}
