package eam.edu.unieventos.ui.screens

import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Item
import eam.edu.unieventos.model.Location
import eam.edu.unieventos.ui.viewmodel.CartViewModel
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.ui.viewmodel.ItemViewModel
import eam.edu.unieventos.ui.viewmodel.LocationsViewModel
import eam.edu.unieventos.utils.SharedPreferenceUtils
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventDetails(eventCode: String, onBack: () -> Unit, onViewCart: () -> Unit) {
    val context = LocalContext.current
    var firstRender = true
    val eventViewModel: EventsViewModel = remember { EventsViewModel() }
    val locationViewModel: LocationsViewModel = remember { LocationsViewModel() }
    val cartViewModel: CartViewModel = remember { CartViewModel() }
    val itemViewModel: ItemViewModel = remember { ItemViewModel() }
    var event by remember { mutableStateOf(Event()) }

    val selectedTickets = remember { mutableStateListOf<Pair<Int, Location>>() }
    var userLogged = SharedPreferenceUtils.getCurrenUser(context)

    var user by remember { mutableStateOf<Client?>(null) }
    var cart by remember { mutableStateOf<Cart?>(null) }

    LaunchedEffect(userLogged) {
        userLogged?.let {
            user = ClientsViewModel(context).getByUserId(it.id) as Client?
            cart = user?.let { it1 -> cartViewModel.getCartByClient(it1.id) }
        }
    }

    val scrollState = rememberScrollState()

    if (event == null) {
        Text(text = stringResource(id = R.string.event_not_found), modifier = Modifier.padding(16.dp))
        return
    }

    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    LaunchedEffect(eventCode) {
        event = eventViewModel.getEventByCode(eventCode)!!
        locations  = locationViewModel.getLocationsByEventCode(eventCode)

    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (event.poster != null) {
            Image(
                painter = rememberImagePainter(data = event.poster),
                contentDescription = stringResource(id = R.string.poster_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.poster_placeholder),
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.date_label), fontSize = 16.sp, color = Color.Gray)
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(event.dateEvent),
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = stringResource(id = R.string.city_label), fontSize = 16.sp, color = Color.Gray)
                Text(text = event.city, fontSize = 18.sp)
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.time_label), fontSize = 16.sp, color = Color.Gray)
                Text(text = event.time.toString(), fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = stringResource(id = R.string.place_label), fontSize = 16.sp, color = Color.Gray)
                Text(text = event.place, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.select_location),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))
        val cartItems = cart?.let { itemViewModel.loadItemsByCart(it.id) }
        locations.forEach { location ->
            var ticketCount by remember {
                mutableStateOf(cartItems?.find { it.locationId == location.id }?.ticketQuantity ?: 0)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = location.name, fontSize = 18.sp)

                val priceFormatted = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(location.price)
                Text(text = priceFormatted, fontSize = 16.sp, color = Color.Gray)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (ticketCount > 0) ticketCount -= 1 }) {
                        Icon(Icons.Default.Remove, contentDescription = stringResource(id = R.string.decrement))
                    }

                    Text(text = "$ticketCount")

                    IconButton(onClick = { ticketCount += 1 }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.increment))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LaunchedEffect(ticketCount) {
                if (!firstRender) {
                    // Eliminar si ya existe un par con esta ubicación
                    selectedTickets.removeAll { it.second.id == location.id }

                    // Solo agregar si ticketCount es mayor a 0
                    if (ticketCount > 0) {
                        selectedTickets.add(Pair(ticketCount, location))
                    }

                }
            }

        }

        if(firstRender){
            firstRender = false
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                addItem(cart, cartItems, event, selectedTickets, context, itemViewModel, cartViewModel)
//                for (ticket in selectedTickets) {
//                    if (ticket.first == 0) {
//                        selectedTickets.remove(ticket)
//                    }
//                }
            }
        ) {
            Text(text = stringResource(id = R.string.addCart))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onViewCart,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text(text = stringResource(id = R.string.view_cart))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = stringResource(id = R.string.back))
        }
    }
}

fun addItem(cart: Cart?, cartItems: List<Item>?, event: Event, selectedTickets: List<Pair<Int, Location>>, context: Context, itemViewModel: ItemViewModel, cartViewModel: CartViewModel) {
    if (cart != null && cartItems != null) {
        // Primero, identificamos las ubicaciones seleccionadas actualmente
        val selectedLocationIds = selectedTickets.map { it.second.id }
        var amount = 0f
        // Borramos los items que ya no están seleccionados (cantidad 0)
        cartItems.forEach { cartItem ->
            if (!selectedLocationIds.contains(cartItem.locationId)) {
                // Si el item ya no está en selectedTickets, significa que se puso en 0
                itemViewModel.removeItem(cartItem.id)
            }
        }

        // Ahora procesamos los tickets seleccionados
        for (ticket in selectedTickets) {
            var founded = false

            // Actualizamos los items existentes
            for (cartItem in cartItems) {
                if (cartItem.locationId == ticket.second.id) {
                    founded = true
                    cartItem.ticketQuantity = ticket.first
                    cartItem.totalPrice = ticket.first * ticket.second.price
                    itemViewModel.updateItem(cartItem)
                    amount = amount?.plus(cartItem.totalPrice)!!
                    break
                }
            }

            // Agregamos nuevos items
            if (!founded && ticket.first > 0) {
                val item = Item(
                    id = "",
                    cartId = cart.id,
                    eventCode = event.code,
                    locationId = ticket.second.id,
                    ticketQuantity = ticket.first,
                    totalPrice = ticket.first * ticket.second.price,
                    orderCode = ""
                )
                itemViewModel.addItem(item)
                amount = amount?.plus(item.totalPrice)!!
            }
        }
        cart.total = amount
        cartViewModel.updateCart(cart)
    } else {
        Toast.makeText(context, context.getString(R.string.cart_add_error), Toast.LENGTH_SHORT).show()
    }
}