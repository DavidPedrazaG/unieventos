package eam.edu.unieventos.ui.screens

import android.app.TimePickerDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Location
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.ui.viewmodel.LocationsViewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEvent(eventCode: String, onBack: () -> Unit) {
    // Obtener el contexto y los viewModels
    val context = LocalContext.current
    val eventViewModel: EventsViewModel = remember { EventsViewModel(context) }
    val locationViewModel: LocationsViewModel = remember { LocationsViewModel(context) }

    // Obtener el evento que se va a editar
    //eventViewModel.logAllEvents()
    val event = eventViewModel.getEventByCode(eventCode)
    var name by remember { mutableStateOf(event?.name ?: "") }
    var address by remember { mutableStateOf(event?.place ?: "") }
    var city by remember { mutableStateOf(event?.city ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var type by remember { mutableStateOf(event?.type ?: "") }
    var poster by remember { mutableStateOf(event?.poster ?: "") }
    var locationImage by remember { mutableStateOf(event?.locationImage ?: "") }
    var dateEvent by remember { mutableStateOf(event?.dateEvent) }
    var isActive by remember { mutableStateOf(event?.isActive ?: true) }

    var timeEvent by remember { mutableStateOf<LocalTime?>(event?.time) } // Nueva variable para la hora del evento


    // Localidades del evento
    val initialLocations = event?.locations?.mapNotNull { locationViewModel.getLocationById(it) } ?: emptyList()
    var numberOfLocations by remember { mutableStateOf(initialLocations.size) }
    var locationNames = remember { mutableStateListOf<String>().apply { addAll(initialLocations.map { it.name }) } }
    var locationPrices = remember { mutableStateListOf<String>().apply { addAll(initialLocations.map { it.price.toString() }) } }
    var locationMaxCapacity = remember { mutableStateListOf<String>().apply { addAll(initialLocations.map { it.maxCapacity.toString() }) } }

    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()

    // Scroll state para permitir desplazamiento
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState), // Añade el scroll aquí
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo para el código del evento
            OutlinedTextField(
                value = event?.code ?: "",
                onValueChange = {}, // No se puede cambiar el código
                label = { Text(text = "Código del Evento") },
                readOnly = true, // Solo lectura
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            // Campos para la información del evento
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Nombre del Evento") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = "Sitio") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(text = "Ciudad") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Descripción") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = type,
                onValueChange = { type = it },
                label = { Text(text = "Tipo de Evento") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = poster,
                onValueChange = { poster = it },
                label = { Text(text = "URL del Póster") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = locationImage,
                onValueChange = { locationImage = it },
                label = { Text(text = "URL de la Imagen de Ubicación") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            // Fecha del evento
            OutlinedTextField(
                value = dateEvent?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
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

            // Hora del evento
            OutlinedTextField(
                value = timeEvent?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(text = "Seleccionar Hora") },
                trailingIcon = {
                    IconButton(onClick = {
                        // Mostrar TimePickerDialog para seleccionar la hora
                        val timePickerDialog = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                timeEvent = LocalTime.of(hourOfDay, minute)
                            },
                            timeEvent?.hour ?: 0, // Hora por defecto
                            timeEvent?.minute ?: 0, // Minutos por defecto
                            true
                        )
                        timePickerDialog.show()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Icon Time"
                        )
                    }
                },
                modifier = Modifier.width(190.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para seleccionar cuántas localidades se quieren
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (numberOfLocations > initialLocations.size ) {
                        numberOfLocations -= 1
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

            // Componente dinámico para editar localidades
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until numberOfLocations) {
                    LocationRow(
                        index = i + 1,
                        locationName = locationNames[i] , // No se puede modificar
                        onNameChange = {
                            if (i >= initialLocations.size) {
                                locationNames[i] = it // Solo se puede editar si i es mayor o igual a initialLocation.size
                            }
                        }, // No se permite cambiar el nombre
                        price = locationPrices[i],
                        onPriceChange = { locationPrices[i] = it },
                        maxCapacity = locationMaxCapacity[i],
                        onMaxChange = { locationMaxCapacity[i] = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Botón para guardar los cambios
            Button(onClick = {
                if (event != null) {
                    // Actualizar las localidades
                    val updatedLocations = mutableListOf<Location>()
                    for (i in 0 until numberOfLocations) {
                        if (locationNames[i].isNotEmpty() && locationPrices[i].isNotEmpty() && locationMaxCapacity[i].isNotEmpty()) {
                            val location = Location(
                                id = if (i < initialLocations.size) initialLocations[i].id else eventViewModel.generateRandomCode(6),
                                name = locationNames[i],
                                price = locationPrices[i].toFloat(),
                                eventCode = event.code,
                                maxCapacity = locationMaxCapacity[i].toInt(),
                                ticketsSold = initialLocations.getOrNull(i)?.ticketsSold ?: 0,
                                isActive = true
                            )
                            updatedLocations.add(location)
                        }
                    }

                    // Actualizar el evento
                    val updatedEvent = Event(
                        id = event.id,
                        code = event.code,
                        name = name,
                        place = address,
                        city = city,
                        description = description,
                        type = type,
                        poster = poster,
                        locationImage = locationImage,
                        locations = updatedLocations.map { it.id },
                        dateEvent = dateEvent ?: event.dateEvent,
                        time =   timeEvent ?: LocalTime.now(),
                        isActive = isActive
                    )

                    eventViewModel.editEvent(updatedEvent)
                    updatedLocations.forEach { location ->
                        if (initialLocations.any { it.id == location.id }) {
                            // Si la localidad ya existe (mismo ID), la actualiza
                            locationViewModel.updateLocation(location)
                        } else {
                            // Si la localidad no existe (es nueva), la agrega
                            locationViewModel.createLocation(location)
                        }
                    }

                    Toast.makeText(context, "Evento actualizado correctamente", Toast.LENGTH_SHORT).show()
                    onBack()
                }
            }) {
                Text(text = "Actualizar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para cancelar la edición
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    }
}
