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
import eam.edu.unieventos.R
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel
import eam.edu.unieventos.utils.SharedPreferenceUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPurchaseHistory: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    clientsViewModel: ClientsViewModel = remember { ClientsViewModel(context) }
) {
    val context = LocalContext.current
    val clientId = SharedPreferenceUtils.getCurrenUser(context)?.id
    var friends by remember { mutableStateOf<List<Client>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(clientId) {
        clientId?.let { id ->
            clientsViewModel.getFriendList(
                userId = id,
                onSuccess = { friendIds ->
                    scope.launch {
                        val allClients = clientsViewModel.getClientsList()
                        friends = allClients.filter { friendIds.contains(it.id) }
                    }
                },
                onFailure = { exception ->

                }
            )
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                modifier = Modifier,
                selected = 1,
                context = context,
                onNavegateToSettings = onNavigateToSettings,
                onNavegateToPurchaseHistory = onNavigateToPurchaseHistory,
                onNavegateToNotifications = onNavigateToNotifications,
                onNavegateToHome = onNavigateToHome,
                onNavegateToCoupons = onNavigateToCoupons
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

            if (friends.isNotEmpty()) {
                LazyColumn {
                    items(friends) { friend ->
                        FriendCard(friend = friend)
                    }
                }
            } else {
                Text("No tienes amigos agregados aún.")
            }
        }
    }
}

@Composable
fun FriendCard(friend: Client) {
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
            Text(text = friend.name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

