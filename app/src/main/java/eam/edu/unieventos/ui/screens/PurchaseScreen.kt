package eam.edu.unieventos.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.TextField
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
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.model.Item
import eam.edu.unieventos.model.Order
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar
import eam.edu.unieventos.ui.viewmodel.CartViewModel
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import eam.edu.unieventos.ui.viewmodel.CouponsViewModel
import eam.edu.unieventos.ui.viewmodel.ItemViewModel
import eam.edu.unieventos.ui.viewmodel.LocationsViewModel
import eam.edu.unieventos.ui.viewmodel.OrderViewModel
import eam.edu.unieventos.utils.SharedPreferenceUtils
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
                    Text(text = "Event: ${item.eventCode}", fontSize = 16.sp)
                    Text(text = "Location: ${item.locationId}", fontSize = 14.sp)
                    Text(text = "Cantidad de tiquets: ${item.ticketQuantity}", fontSize = 14.sp)
                }
            }


            Button(onClick = {
                if(userLogged != null) {
                    if(userLogged.rol == "Client"){
                        onNavegateToEventDetail(item.eventCode)
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
    var couponCode by remember { mutableStateOf("") }
    val orderViewModel: OrderViewModel = remember { OrderViewModel() }
    val cartViewModel: CartViewModel = remember { CartViewModel() }
    val itemViewModel: ItemViewModel = remember { ItemViewModel() }
    val locationViewModel: LocationsViewModel = remember { LocationsViewModel() }
    val couponsVimodel: CouponsViewModel = remember { CouponsViewModel() }
    var userLogged = SharedPreferenceUtils.getCurrenUser(context)
    var date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date()))
    var client: Client? by remember { mutableStateOf(null) }
    var cart: Cart? by remember { mutableStateOf(null) }
    var coupon: Coupon? by remember { mutableStateOf(null) }
    var amount: Float? by remember { mutableStateOf(0f) }

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
        cart = cartId?.let { cartViewModel.getCartById(it) }
        amount = cart?.total
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
                    for(item in cartItems){
                        ItemCard(
                            item = item,
                            userLogged = userLogged,
                            onNavegateToEventDetail = {
                                onNavegateToEventDetail(item.eventCode)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                Box{
                    Row {
                        TextField(
                            value = couponCode,
                            onValueChange = { couponCode = it },
                            label = { Text(text = "Código de cupón") },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(vertical = 8.dp),
                            singleLine = true
                        )
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(vertical = 8.dp),
                            onClick = {
                                if(couponCode != ""){
                                    if(couponCode != coupon?.code){
                                        coupon = couponsVimodel.validateCoupon(couponCode)
                                        if(coupon != null) {
                                            cart = cartItems?.let {
                                                orderViewModel.validateCoupon(
                                                    coupon!!, client?.id!!,
                                                    it, cart!!
                                                )
                                            }
                                            if (amount != cart?.total) {
                                                Toast.makeText(
                                                    context,
                                                    "Cupón aplicado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                amount = cart?.total
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Cupón ya usado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }else{
                                            Toast.makeText(context, "El cupón no es valido", Toast.LENGTH_SHORT).show()
                                            couponCode = ""
                                        }
                                    }else{
                                        Toast.makeText(context, "El cupón ya fue aplicádo", Toast.LENGTH_SHORT).show()
                                        couponCode = ""
                                    }
                                }else{
                                    Toast.makeText(context, "Ingrese un cupón", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ){
                            Text(text = "Aplicar")
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Total a pagar: ${amount}")
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    if(cartItems != null){
                        cartItems.forEach { item ->
                            var isValid = locationViewModel.validateTicket(item)
                            if(!isValid){
                                Toast.makeText(context, "No hay tickets disponibles para el evento ${item.eventCode} en la ubicación ${item.locationId}", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                        }
                        val order = amount?.let {
                            Order(
                                id = "",
                                code = "",
                                clientId = client?.id ?: "",
                                usedCoupon = "",
                                totalAmount = it,
                                purchaseDate = date,
                                paymentDay = date,
                                isActive = true
                            )
                        }
                        if (order != null) {
                            order.code = orderViewModel.addOrder(order)
                        }
                        couponsVimodel.redeemCoupon(couponCode)
                        itemViewModel.emptyCart(cartId, order?.code!!)
                        cartItems.forEach { item ->
                            locationViewModel.buyTicket(item)
                        }
                        onNavegateToHome()
                    }
                }){
                    Text(text = "Comprar")
                }
            }
        }
    } else {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Cargando datos del usuario...")
        }
    }
}
