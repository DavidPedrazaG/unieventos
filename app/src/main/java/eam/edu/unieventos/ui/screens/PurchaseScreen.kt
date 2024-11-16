package eam.edu.unieventos.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R
import eam.edu.unieventos.dto.UserDTO
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.Item
import eam.edu.unieventos.model.Order
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import eam.edu.unieventos.ui.viewmodel.ItemViewModel
import eam.edu.unieventos.ui.viewmodel.OrderViewModel
import eam.edu.unieventos.utils.SharedPreferenceUtils
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ItemCard(item: Item, userLogged: UserDTO?, onNavegateToEventDetail: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_image),
                    contentDescription = "Evento imagen",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))


            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (item != null) {
                    Text(text = "Event: ${item.eventId}", fontSize = 16.sp)
                    Text(text = "Location: ${item.locationId}", fontSize = 14.sp)
                    Text(text = "Cantidad de tiquets: ${item.ticketQuantity}", fontSize = 14.sp)
                }
            }


            Button(onClick = {
                if(userLogged != null) {
                    if(userLogged.rol == "Client"){
                        onNavegateToEventDetail(item.eventId)
                    }
                }
            }) {
                if(userLogged != null) {
                    if(userLogged.rol == "Client"){
                        Text(text = "VER DETALLES")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onNavegateToSettings: () -> Unit,
    onNavegateToNotifications: () -> Unit,
    onNavegateToPurchaseHistory: () -> Unit,
    onNavegateToHome: () -> Unit,
    onNavegateToCoupons: () -> Unit,
    onNavegateToEventDetail: (String) -> Unit
) {
    val orderViewModel: OrderViewModel = remember { OrderViewModel() }
    val itemViewModel: ItemViewModel = remember { ItemViewModel() }
    var userLogged = SharedPreferenceUtils.getCurrenUser(context)
    var date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date()))
    var client: Client? by remember { mutableStateOf(null) }


    if (userLogged != null) {
        LaunchedEffect(userLogged) {
            try {

                Log.d("PurchaseScreen", "Obteniendo cliente para el usuario: ${userLogged.id}")
                val userClient = ClientsViewModel(context).getByUserId(userLogged.id)


                if (userClient is Client) {
                    client = userClient
                    Log.d("PurchaseScreen", "Cliente encontrado: ${client?.id}")
                } else {
                    Log.e("PurchaseScreen", "El usuario no es un Client. Usuario: ${userLogged.id}")
                }
            } catch (e: Exception) {
                Log.e("PurchaseScreen", "Error al obtener el cliente: ${e.message}")
            }
        }
    } else {
        Log.e("PurchaseScreen", "Usuario no está logueado.")
    }


    if (client != null) {
        val cartId = client?.cartId

        Scaffold(
            bottomBar = {
                CustomBottomNavigationBar(
                    modifier = Modifier,
                    selected = 0,
                    context = context,
                    onNavegateToSettings = onNavegateToSettings,
                    onNavegateToPurchaseHistory = onNavegateToPurchaseHistory,
                    onNavegateToNotifications = onNavegateToNotifications,
                    onNavegateToHome = onNavegateToHome,
                    onNavegateToCoupons = onNavegateToCoupons
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val cartItems = cartId?.let { itemViewModel.loadItemsByCart(it) }
                Box {
                    Text(text = "Carrito de compras")
                }
                if (cartItems != null) {
                    cartItems.forEach { item ->
                        ItemCard(
                            item = item,
                            userLogged = userLogged,
                            onNavegateToEventDetail = {
                                onNavegateToEventDetail(item.eventId)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                Box{
                    Button(onClick = {
                        if(cartItems != null){
                            var totalAmount = 0.0f
                            cartItems.forEach { item ->
                                totalAmount += item.ticketQuantity * item.totalPrice
                            }
                            val order = Order(
                                id = "",
                                code = "",
                                clientId = client?.id ?: "",
                                usedCoupon = "",
                                totalAmount = totalAmount,
                                purchaseDate = date,
                                paymentDay = date,
                                isActive = true
                            )
                            order.id = orderViewModel.addOrder(order)
                            itemViewModel.emptyCart(cartId, order.id)
                            onNavegateToHome()
                        }
                    }){
                        Text(text = "Comprar")
                    }
                }
            }
        }
    } else {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Cargando datos del usuario...")
        }
    }
}
