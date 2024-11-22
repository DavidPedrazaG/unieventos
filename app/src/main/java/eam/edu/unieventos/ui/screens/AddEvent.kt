package eam.edu.unieventos.ui.screens

import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Location
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.ui.viewmodel.LocationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEvent(
        onBack: () -> Unit,
        onNavegateToHome: (String) -> Unit

) {


    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var poster by remember { mutableStateOf("") }
    var locationImage by remember { mutableStateOf("") }
    var dateEvent by remember { mutableStateOf<Date?>(null) }
    var timeEvent by remember { mutableStateOf<LocalTime?>(null) } // Nueva variable para la hora del evento
    val isActive = true

    val config = mapOf(
        "cloud_name" to "deofyzexo",
        "api_key" to "115375694432889",
        "api_secret" to "CxeQ6T4qUK9Innz0lAQP9CCqjLg"
    )

    val cloudinary = Cloudinary(config)
    val scope = rememberCoroutineScope()

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

    var events = listOf(
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

    // Variables para localidades dinámicas
    var numberOfLocations by remember { mutableStateOf(1) }
    var locationNames = remember { mutableStateListOf<String>().apply { add("") } }
    var locationPrices = remember { mutableStateListOf<String>().apply { add("") } }
    var locationMaxCapacity = remember { mutableStateListOf<String>().apply { add("") } }

    var expandedDate by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()

    val context = LocalContext.current
    val eventViewModel: EventsViewModel = remember { EventsViewModel() }
    val locationViewModel: LocationsViewModel = remember { LocationsViewModel() }

    // Scroll state para permitir desplazamiento
    val scrollState = rememberScrollState()

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.e("URII", uri.toString())

            scope.launch(Dispatchers.IO) {
                val imputStream = context.contentResolver.openInputStream(uri)
                imputStream?.use { stream ->
                    val result = cloudinary.uploader().upload(stream , ObjectUtils.emptyMap())
                    Log.e("resultoleeeeee", result.toString())
                    locationImage = result["secure_url"].toString()


                }
            }

        }
    }

    val posterFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.e("URI", uri.toString())

            scope.launch(Dispatchers.IO) {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val result = cloudinary.uploader().upload(stream, ObjectUtils.emptyMap())
                    Log.e("resulttttt", result.toString())
                    poster = result["secure_url"].toString()
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Box para el diseño general de la pantalla
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
                label = { Text(text = stringResource(id = R.string.event_location)) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))


            // para la ciudad
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ciudad:",
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
                    text = "Tipo:",
                    color = Color.Black,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(30.dp))

                DropdownMenu(
                    value = type,
                    onValeChange = {
                        type = it
                    },
                    items = events
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
                        .wrapContentWidth()) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Rounded.Upload
                    )
                }
            }

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
                    placeholder = { Text(text = stringResource(id = R.string.date)) },
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
                                        // Convertir a Calendar para manipular fácilmente
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = selectedDay
                                        // Añadir un día
                                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                                        // Asignar la nueva fecha
                                        //                                    dateEvent = calendar.time
                                        dateEvent = java.sql.Date(calendar.getTime().getTime());
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
                    placeholder = { Text(text = stringResource(id = R.string.hour)) },
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
                                imageVector = Icons.Rounded.Timer,
                                contentDescription = "Icon Time"
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
                modifier = Modifier.fillMaxWidth(0.95f),
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
                if (!areFieldsNotEmpty(name, address, city, description, type, poster, locationImage,   dateEvent, timeEvent)) {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if(type.equals("Select a Value")){
                    Toast.makeText(context, "Seleccione un tipo de evento", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val eventCode = eventViewModel.generateRandomCode(6);
                val locations = mutableListOf<Location>()

                for (i in 0 until numberOfLocations) {
                    if (locationNames[i].isNotEmpty() && locationPrices[i].isNotEmpty() && locationMaxCapacity[i].isNotEmpty()) {
                        // Agregar la localidad a la lista solo si todos los campos no están vacíos
                        val location = Location(
                            id = "",
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
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                val timeString = timeEvent?.format(timeFormatter) ?: LocalTime.now().format(timeFormatter)
                val newEvent = dateEvent?.let {
                    Event(
                        id = "",
                        code = eventCode ,
                        name = name,
                        place = address,
                        city = city,
                        description = description,
                        type = type,
                        poster = poster,
                        locationImage = locationImage,
                        //locations = locations.map { it.id },
                        dateEvent = it,
                        time = timeString, // Hora del evento
                        isActive = isActive
                    )
                }

                if (newEvent != null ) {
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
                    timeEvent = null // Limpiar la hora también

                    numberOfLocations = 1
                    locationNames.clear()
                    locationPrices.clear()
                    locationMaxCapacity.clear()
                    locationNames.add("")
                    locationPrices.add("")
                    locationMaxCapacity.add("")
                    onNavegateToHome("Admin")



                } else {
                    Toast.makeText(context, "Por favor selecciona una fecha", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(stringResource(id = R.string.labelCreateLocation))
            }
            Spacer(modifier = Modifier.height(15.dp))

            // Botón para cancelar la edición
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )) {
                Text(stringResource(id = R.string.cancel))
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

fun areFieldsNotEmpty(
    name: String,
    address: String,
    city: String,
    description: String,
    type: String,
    poster: String,
    locationImage: String,
    dateEvent: Date?,
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

