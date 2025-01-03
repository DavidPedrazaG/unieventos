package eam.edu.unieventos.ui.screens

import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import java.time.format.DateTimeFormatter
import java.util.*
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.rounded.Upload
import androidx.core.content.ContextCompat
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEvent(eventCode: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val eventViewModel: EventsViewModel = remember { EventsViewModel() }
    val locationViewModel: LocationsViewModel = remember { LocationsViewModel() }

    var event by remember { mutableStateOf(Event()) }


    var name by remember { mutableStateOf(event?.name ?: "") }
    var address by remember { mutableStateOf(event?.place ?: "") }
    var city by remember { mutableStateOf(event?.city ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var type by remember { mutableStateOf(event?.type ?: "") }

    var poster by remember { mutableStateOf(event?.poster ?: "") }
    var locationImage by remember { mutableStateOf(event?.locationImage ?: "") }
    var dateEvent by remember { mutableStateOf(event?.dateEvent) }
    var isActive by remember { mutableStateOf(event?.isActive ?: true) }

    //var timeEvent by remember { mutableStateOf<LocalTime?>(event?.time) } // Nueva variable para la hora del evento
    var timeEvent by remember { mutableStateOf<LocalTime?>(
        event?.time?.let { LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm")) }
    ) }

    var cities = listOf(
        "Arauca", "Armenia", "Barranquilla", "Bogotá",
        "Bucaramanga", "Cali", "Cartagena", "Cúcuta",
        "Florencia", "Ibagué", "Inírida", "Leticia",
        "Manizales", "Medellín", "Mitú", "Mocoa",
        "Montería", "Neiva", "Pasto", "Pereira",
        "Popayán", "Puerto Carreño", "Quibdó", "Riohacha",
        "San Andrés", "San José del Guaviare", "Santa Marta", "Sincelejo",
        "Tunja", "Valledupar", "Villavicencio", "Yopal"
    )

    var eventsType = listOf(
        stringResource(R.string.conference),
        stringResource(R.string.festival),
        stringResource(R.string.workshop),
        stringResource(R.string.exhibition),
        stringResource(R.string.marathon),
        stringResource(R.string.tournament),
        stringResource(R.string.fair),
        stringResource(R.string.competition),
        stringResource(R.string.seminar),
        stringResource(R.string.concert)
    )


    var initialLocations by remember { mutableStateOf<List<Location>>(emptyList()) }

    var numberOfLocations by remember { mutableStateOf(0) }




    var locationNames = remember { mutableStateListOf<String>().apply { addAll(initialLocations.map { it.name }) } }
    var locationPrices = remember { mutableStateListOf<String>().apply { addAll(initialLocations.map { it.price.toString() }) } }
    var locationMaxCapacity = remember { mutableStateListOf<String>().apply { addAll(initialLocations.map { it.maxCapacity.toString() }) } }

    // Variables de UI adicionales
    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()
    val scrollState = rememberScrollState()
    var showDeactivateDialog by remember { mutableStateOf(false) }

    val config = mapOf(
        "cloud_name" to "deofyzexo",
        "api_key" to "115375694432889",
        "api_secret" to "CxeQ6T4qUK9Innz0lAQP9CCqjLg"
    )

    val cloudinary = Cloudinary(config)
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }



    LaunchedEffect(eventCode) {
        event = eventViewModel.getEventByCode(eventCode)!!
        name = event?.name ?: ""
        address = event?.place   ?: ""
        description = event?.description ?: ""
        poster = event?.poster ?: ""
        locationImage = event?.locationImage ?: ""
        city = event?.city   ?: ""
        type = event?.type ?: ""
        dateEvent = event?.dateEvent ?: Date()
        val timeString = event?.time ?: "00:00"
        timeEvent = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))

        // Cargar las localidades
        initialLocations = locationViewModel.getLocationsByEventCode(eventCode)
        numberOfLocations = initialLocations.size // Actualizar el número de localidades

        // Limpiar y actualizar las listas con las localidades iniciales
        locationNames.clear()
        locationPrices.clear()
        locationMaxCapacity.clear()

        initialLocations.forEach { location ->
            locationNames.add(location.name)
            locationPrices.add(location.price.toString())
            locationMaxCapacity.add(location.maxCapacity.toString())
        }

    }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.e("URI", uri.toString())

            scope.launch(Dispatchers.IO) {
                val imputStream = context.contentResolver.openInputStream(uri)
                imputStream?.use { stream ->
                    val result = cloudinary.uploader().upload(stream , ObjectUtils.emptyMap())
                    Log.e("resultt", result.toString())
                    locationImage = result["secure_url"].toString()  // Esta actualización no recompone la UI
                }
            }

        }
    }

    val posterFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.e("URI", uri.toString())

                scope.launch(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    inputStream?.use { stream ->
                        val result = cloudinary.uploader().upload(stream, ObjectUtils.emptyMap())
                        Log.e("resultolee", result.toString())
                        poster = result["secure_url"].toString()  // Esta actualización no recompone la UI
                    }
                }
            }
        }



    Scaffold(
    ) {paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .verticalScroll(scrollState), // Añade el scroll aquí
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            // Campo para el código del evento
            OutlinedTextField(
                value = event?.code ?: "",
                onValueChange = {}, // No se puede cambiar el código
                label = { Text(text = stringResource(id = R.string.event_code_label)) },
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
                label = { Text(text = stringResource(id = R.string.event_name)) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = stringResource(id = R.string.event_location))},
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))


            // para la ciudad
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.city_label),
                    color = Color.Black,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                DropdownMenu(
                    value = city,
                    onValeChange = {
                        city = it
                    },
                    items = cities
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = stringResource(id = R.string.event_description)) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // para el tipo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.type_label),
                    color = Color.Black,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(30.dp))

                DropdownMenu(
                    value = type,
                    onValeChange = {
                        type = it
                    },
                    items = eventsType
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                TextField(
                    value = poster,
                    onValueChange = { poster = it },
                    label = { Text(text = stringResource(id = R.string.poster_url)) },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(vertical = 8.dp),
                    singleLine = true
                )

                Button(onClick = {
                    val permissionCheckResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.READ_MEDIA_IMAGES
                        )
                    } else {
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        posterFileLauncher.launch("image/*")
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp)
                        .wrapContentWidth()) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Rounded.Upload
                    )
                }
            }

            Row(){
                TextField(
                    value = locationImage,
                    onValueChange = { locationImage = it },
                    label = { Text(text = stringResource(id = R.string.location_image_url)) },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(vertical = 8.dp),
                    singleLine = true
                )

                Button(onClick = {
                    val permissionCheckResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.READ_MEDIA_IMAGES

                        )
                    } else {
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE

                        )
                    }
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        fileLauncher.launch("image/*")
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

                        }
                    }
                },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp)
                        .wrapContentWidth()

                    ) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Rounded.Upload
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Fecha del evento
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.date),
                    color = Color.Black,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(50.dp))

                OutlinedTextField(
                    value = dateEvent?.let {
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(it)
                    } ?: "",
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
            }
            if (expandedDate) {
                DatePickerDialog(
                    onDismissRequest = { expandedDate = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDay = datePickerState.selectedDateMillis
                                if (selectedDay != null) {
                                    // Convertir a Calendar para manipular fácilmente
                                    val calendar = Calendar.getInstance()
                                    calendar.timeInMillis = selectedDay
                                    // Añadir un día
                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                    // Asignar la nueva fecha
                                    dateEvent = calendar.time
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.hour),
                    color = Color.Black,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(50.dp))
                OutlinedTextField(
                    value = timeEvent?.toString() ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(text = stringResource(id = R.string.select_time)) },
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
                                contentDescription = stringResource(id = R.string.icon_date)
                            )
                        }
                    },
                    modifier = Modifier.width(190.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = stringResource(id = R.string.location),
                color = Color.Black,
                fontSize = 20.sp
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
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = stringResource(id = R.string.increment))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Componente dinámico para editar localidades
            Column(
                modifier = Modifier.fillMaxWidth(0.95f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until numberOfLocations) {
                    LocationRow(
                        index = i + 1,
                        locationName = locationNames[i],
                        onNameChange = {
                            if (i >= initialLocations.size) {
                                locationNames[i] = it // Solo se puede editar si i es mayor o igual a initialLocations.size
                            }
                        },
                        price = locationPrices[i],
                        onPriceChange = { locationPrices[i] = it },
                        maxCapacity = locationMaxCapacity[i],
                        onMaxChange = { locationMaxCapacity[i] = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Botón para guardar los cambios
            Button(onClick = {
                if (!areFieldsNotEmptyEdit(name, address, city, description, type, poster, locationImage ,dateEvent, timeEvent)) {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
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

                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val timeString = timeEvent?.format(timeFormatter) ?: LocalTime.now().format(timeFormatter)
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
                        dateEvent = dateEvent ?: event.dateEvent,
                        time =   timeString,
                        isActive = true
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
                    Toast.makeText(context, context.getString(R.string.event_updated), Toast.LENGTH_SHORT).show()
                    onBack()
                }
            }) {
                Text(text = stringResource(id = R.string.update_event))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para desactivar el cupón
            Button(
                onClick = { showDeactivateDialog = true }, // Activar el diálogo de confirmación
                //modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.deactivate))
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Botón para cancelar la edición
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )) {
                Text(stringResource(id = R.string.cancel))
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (showDeactivateDialog) {
                AlertDialog(
                    onDismissRequest = { showDeactivateDialog = false },
                    title = { Text(text = stringResource(id = R.string.confirm_deactivation)) },
                    text = { Text(text = stringResource(id = R.string.deactivate_confirmation)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                eventViewModel.deactivateEvent(event)
                                showDeactivateDialog = false
                                onBack()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeactivateDialog = false }
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}




fun areFieldsNotEmptyEdit(
    name: String,
    address: String,
    city: String,
    description: String,
    type: String,
    poster: String,
    locationImage: String,
    dateEvent: java.util.Date?,
    timeEvent: LocalTime?
): Boolean {
    return name.isNotEmpty() &&
            address.isNotEmpty() &&
            city.isNotEmpty() &&
            description.isNotEmpty() &&
            type.isNotEmpty() &&
            poster.isNotEmpty() &&
            locationImage.isNotEmpty() &&
            dateEvent != null &&
            timeEvent != null
}