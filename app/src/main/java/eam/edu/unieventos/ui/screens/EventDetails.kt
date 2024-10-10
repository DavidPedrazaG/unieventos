package eam.edu.unieventos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.ui.viewmodel.LocationsViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventDetails(eventCode: String, onBack: () -> Unit, onViewCart: () -> Unit) {
    // Obtener el contexto y los ViewModels
    val context = LocalContext.current
    val eventViewModel: EventsViewModel = remember { EventsViewModel(context) }
    val locationViewModel: LocationsViewModel = remember { LocationsViewModel(context) }

    // Obtener el evento que se va a mostrar
    val event = eventViewModel.getEventByCode(eventCode)
    val selectedTickets = remember { mutableStateListOf<Pair<String, Int>>() } // Localidades y cantidad de entradas

    // Scroll state para permitir desplazamiento
    val scrollState = rememberScrollState()

    // Si el evento es nulo, mostrar un mensaje
    if (event == null) {
        Text(text = "Evento no encontrado", modifier = Modifier.padding(16.dp))
        return
    }

    // Localidades disponibles
    val locations = event.locations.mapNotNull { locationViewModel.getLocationById(it) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Póster principal del evento o cuadro negro simulando un póster
        if (event.poster != null) {
            Image(
                painter = rememberImagePainter(data = event.poster),
                contentDescription = "Póster del evento",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            // Si no hay póster, mostramos un cuadro negro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Poster Placeholder",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre del evento
        Text(
            text = event.name,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = event.description,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Aquí agrego los detalles del evento (hora, fecha, lugar, ciudad) en columnas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Fecha:", fontSize = 16.sp, color = Color.Gray)
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(event.dateEvent),
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Ciudad:", fontSize = 16.sp, color = Color.Gray)
                Text(text = event.city, fontSize = 18.sp)
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Hora:", fontSize = 16.sp, color = Color.Gray)
                Text(text = event.time.toString(), fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Lugar:", fontSize = 16.sp, color = Color.Gray)
                Text(text = event.place, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Localidades disponibles
        Text(
            text = "Selecciona tu localidad:",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Listado de localidades y selección de tickets
        locations.forEach { location ->
            var ticketCount by remember { mutableStateOf(0) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = location.name, fontSize = 18.sp)

                // Precio
                val priceFormatted = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(location.price)
                Text(text = priceFormatted, fontSize = 16.sp, color = Color.Gray)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Botón para restar
                    IconButton(onClick = { if (ticketCount > 0) ticketCount -= 1 }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrementar")
                    }

                    Text(text = "$ticketCount")

                    // Botón para sumar
                    IconButton(onClick = { ticketCount += 1 }) {
                        Icon(Icons.Default.Add, contentDescription = "Incrementar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Almacenar la selección
            LaunchedEffect(ticketCount) {
                if (ticketCount > 0) {
                    selectedTickets.add(location.name to ticketCount)
                } else {
                    selectedTickets.removeAll { it.first == location.name }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para añadir al carrito
        Button(
            onClick = {
                if (selectedTickets.isNotEmpty()) {
                    // Agregar al carrito
                    Toast.makeText(context, "Entradas añadidas al carrito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Selecciona al menos una entrada", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = "Añadir al carrito")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ver el carrito
        Button(
            onClick = onViewCart,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text(text = "Ver carrito")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver atrás
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Volver atrás")
        }
    }
}
