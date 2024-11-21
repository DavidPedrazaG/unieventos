package eam.edu.unieventos.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar
import eam.edu.unieventos.ui.viewmodel.UsersViewModel
import eam.edu.unieventos.ui.viewmodel.UsersViewModelFactory
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import eam.edu.unieventos.utils.SharedPreferenceUtils
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import eam.edu.unieventos.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onNavegateToSettings: () -> Unit,
    onNavegateToNotifications: () -> Unit,
    onNavegateToPurchaseHistory: () -> Unit,
    onNavegateToHome: () -> Unit,
    onNavegateToCoupons: () -> Unit,
    usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(context))
) {
    val context = LocalContext.current
    val client_id = SharedPreferenceUtils.getCurrenUser(context)  // Cliente logueado
    var clients by remember { mutableStateOf<List<Client>>(emptyList()) }
    val clientsViewModel: ClientsViewModel = remember { ClientsViewModel(context) }


    LaunchedEffect(client_id?.id) {
        val loadedClients = clientsViewModel.getClientsList()

        clients = loadedClients.filter { it.id != client_id?.id }
    }

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
            Text(text = stringResource(id = R.string.friends), style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            if (clients.isNotEmpty()) {
                LazyColumn {
                    items(clients) { client ->
                        val isFriend = client_id?.id?.let {

                            client.friends.contains(it) || it in client.friends
                        } ?: false

                        FriendCard(
                            client = client,
                            onAddFriend = { friendId ->
                                client_id?.id?.let { userId ->
                                    if (isFriend) {

                                        clientsViewModel.removeFriend(userId, friendId)

                                        clients = clients.map { client ->
                                            if (client.id == friendId) {
                                                client.copyWithUpdatedFriends(client.friends - userId)
                                            } else {
                                                client
                                            }
                                        }
                                    } else {

                                        clientsViewModel.addFriend(userId, friendId)

                                        clients = clients.map { client ->
                                            if (client.id == friendId) {
                                                client.copyWithUpdatedFriends(client.friends + userId)
                                            } else {
                                                client
                                            }
                                        }
                                    }
                                }
                            },
                            isFriend = isFriend
                        )
                    }
                }
            } else {
                Text("No hay clientes disponibles.")
            }
        }
    }
}

@Composable
fun FriendCard(client: Client, onAddFriend: (String) -> Unit, isFriend: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = client.name, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddFriend(client.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Se debe mostrar "Eliminar amigo" si son amigos, sin importar quien lo haya agregado.
                Text(text = if (isFriend) stringResource(id = R.string.delete_friend) else stringResource(id = R.string.add_friend))
            }
        }
    }
}
