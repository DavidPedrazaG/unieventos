package eam.edu.unieventos.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import eam.edu.unieventos.dto.UserDTO
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.ui.viewmodel.EventsViewModel
import eam.edu.unieventos.utils.SharedPreferenceUtils
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onNavegateToUserConfig:() -> Unit,
    context: Context,
    onNavegateToSettings: () -> Unit,
    onNavegateToNotifications: () -> Unit,
    onNavegateToCoupons: () -> Unit,
    onNavegateToPurchaseHistory: () -> Unit,
    onNavegateToHome: () -> Unit,
    onNavegateToAddEvent: () -> Unit,
    onNavegateToAddCoupon: () -> Unit,
    onNavegateToEventDetail: (String) -> Unit,
    onNavegateToEventEdit: (String) -> Unit,
    onNavegateToPurchase: () -> Unit
) {
    val eventViewModel: EventsViewModel = remember { EventsViewModel() }
    val events = eventViewModel.events.collectAsState().value
    var userLogged = SharedPreferenceUtils.getCurrenUser(context)
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("Armenia") }
    var expandedLocationDropdown by remember { mutableStateOf(false) }
    val locations = listOf("Armenia", "Bogotá", "Medellín")
    var expandedMenu by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton =
        {
            if(userLogged != null) {
                if(userLogged.rol == "Admin"){
                    Box {
                        IconButton(onClick = { expandedMenu = !expandedMenu }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir")
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "Eventos") },
                                onClick = {
                                    onNavegateToAddEvent()
                                })
                            DropdownMenuItem(text = {
                                Text(text = "Cupones") },
                                onClick = {
                                    onNavegateToAddCoupon()
                                })
                        }
                    }
                }
            }
        },
        bottomBar = {
            CustomBottomNavigationBar(
                modifier = Modifier,
                selected = 0, // Indica cuál ítem está seleccionado inicialmente
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
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(Color.Transparent),
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                navigationIcon = {
                    if(userLogged != null) {
                        if(userLogged.rol == "Client"){
                            IconButton(onClick = { onNavegateToUserConfig() }) { // Al hacer clic, navega a la pantalla de configuración
                                Icon(
                                    imageVector =  Icons.Rounded.SupervisedUserCircle,
                                    contentDescription = "Configuración de usuario"
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(imageVector = Icons.Rounded.Logout,
                            contentDescription = null)
                    }
                }

            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar evento") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),

                )
            Spacer(modifier = Modifier.height(8.dp))


            ExposedDropdownMenuBox(
                expanded = expandedLocationDropdown,
                onExpandedChange = { expandedLocationDropdown = !expandedLocationDropdown }
            ) {
                OutlinedTextField(
                    value = selectedLocation,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocationDropdown) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedLocationDropdown,
                    onDismissRequest = { expandedLocationDropdown = false }
                ) {
                    locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location) },
                            onClick = {
                                selectedLocation = location
                                expandedLocationDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if(userLogged != null) {
                if(userLogged.rol == "Client") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { onNavegateToPurchase() }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito de compras"
                            )
                        }
                    }
                }
            }


            LazyColumn {
//                val events = eventViewModel.events.value
                events.forEach { event ->
                    item {
                        EventItem(
                            event=event,
                            userLogged=userLogged,
                            onNavegateToEventDetail = onNavegateToEventDetail,
                            onNavegateToEventEdit = onNavegateToEventEdit)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(
    event: Event?,
    userLogged: UserDTO?,
    onNavegateToEventDetail: (String) -> Unit,
    onNavegateToEventEdit: (String) -> Unit) {

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
                if (event != null) {
                    Text(text = event.name, fontSize = 18.sp)
                    Text(text = "Tipo: ${event.type}", fontSize = 16.sp)
                    Text(text = "Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(event.dateEvent)}", fontSize = 14.sp)
                    Text(text = "Hora: ${event.time.toString()}", fontSize = 14.sp)
                }
            }


            Button(onClick = {
                if(userLogged != null) {
                    if(userLogged.rol == "Client"){
                        onNavegateToEventDetail(event!!.code)
                    }
                    else if(userLogged.rol == "Admin"){
                        onNavegateToEventEdit(event!!.code)
                    }
                }
            }) {
                if(userLogged != null) {
                    if(userLogged.rol == "Client"){
                        Text(text = "VER DETALLES")
                    }
                    else if(userLogged.rol == "Admin"){
                        Text(text = "EDITAR")
                    }
                }
            }
        }
    }

}