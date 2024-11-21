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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
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
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderCard(order: Order) {
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (order != null) {
                    Text(text = "Code: ${order.code}", fontSize = 16.sp)
                    Text(text = "Cantidad pagada: ${order.totalAmount}", fontSize = 14.sp)
                    Text(text = "Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(order.purchaseDate)}", fontSize = 14.sp)
                }
            }


            /*Button(onClick = {
                if(userLogged != null) {
                    if(userLogged.rol == "Client"){
                        onNavegateToEventDetail(order.eventId)
                    }
                }
            }) {
                if(userLogged != null) {
                    if(userLogged.rol == "Client"){
                        Text(text = "VER DETALLES")
                    }
                }
            }*/
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseHistoryScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onNavegateToSettings: () -> Unit,
    onNavegateToNotifications: () -> Unit,
    onNavegateToPurchaseHistory: () -> Unit,
    onNavegateToHome: () -> Unit,
    onNavegateToCoupons: () -> Unit
) {
    val orderViewModel: OrderViewModel = remember { OrderViewModel() }
    val itemViewModel: ItemViewModel = remember { ItemViewModel() }
    var userLogged = SharedPreferenceUtils.getCurrenUser(context)
    var client: Client? by remember { mutableStateOf(null) }

    if (userLogged != null) {
        LaunchedEffect(userLogged) {
            try {
                val userClient = ClientsViewModel(context).getByUserId(userLogged.id)
                if (userClient is Client) {
                    client = userClient
                } else {
                }
            } catch (e: Exception) {
            }
        }
    } else {
    }
    if (client != null) {

        val orders = orderViewModel.getOrdersListByClient(client?.id ?: "")
        Scaffold(
            bottomBar = {
                CustomBottomNavigationBar(
                    modifier = Modifier,
                    selected = 1,
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
                Box {
                    Text(text = stringResource(id = R.string.purchase_history))
                }
                if (orders != null) {
                    orders.forEach { order ->
                        OrderCard(
                            order = order
                        )
                    }
                }
            }
        }
    }
}
