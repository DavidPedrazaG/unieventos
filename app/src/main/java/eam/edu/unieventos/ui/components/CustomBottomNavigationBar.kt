package eam.edu.unieventos.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import eam.edu.unieventos.ui.navigation.RouteScreen
import eam.edu.unieventos.utils.SharedPreferenceUtils

@Composable
fun CustomBottomNavigationBar(
    modifier: Modifier = Modifier,
    selected: Int,
    context: Context,
    onNavegateToSettings: () -> Unit,
    onNavegateToPurchaseHistory: () -> Unit,
    onNavegateToNotifications: () -> Unit,
    onNavegateToCoupons: () -> Unit,
    onNavegateToHome: () -> Unit
) {
    var userLogged = SharedPreferenceUtils.getCurrenUser(context)
    var selectedItem by remember { mutableStateOf(selected) }

    // Definir los ítems de la barra de navegación
    val items = listOf(
        BottomNavItem(stringResource(id = R.string.home), R.drawable.ic_home, isSelected = selectedItem == 0),
        BottomNavItem(stringResource(id = R.string.history), R.drawable.ic_history, isSelected = selectedItem == 1),
        BottomNavItem(stringResource(id = R.string.notifications), R.drawable.ic_notifications, isSelected = selectedItem == 2),
        BottomNavItem("Coupons", R.drawable.ic_coupon, isSelected = selectedItem == 2),
        BottomNavItem(stringResource(id = R.string.settings), R.drawable.ic_settings, isSelected = selectedItem == 3)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background), // Usamos colorScheme.background para fondo
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            if (userLogged != null) {
                if (userLogged.rol == "Client" && index == 3) {
                    return@forEachIndexed
                } else if (userLogged.rol == "Admin" && index == 2) {
                    return@forEachIndexed
                }
                BottomNavigationItemView(
                    item = item,
                    onClick = {
                        selectedItem = index
                        if (index == 0) { // Si el ítem seleccionado es "Home"
                            onNavegateToHome()
                        } else if (index == 1) {
                            onNavegateToPurchaseHistory()
                        } else if (index == 2) {
                            onNavegateToNotifications()
                        } else if (index == 3) {
                            onNavegateToCoupons()
                        } else if (index == 4) {
                            onNavegateToSettings()
                        }
                    },
                    isSelected = item.isSelected
                )
            }
        }
    }
}

// BottomNavigationItemView como antes, ajustado a los colores del tema
@Composable
fun BottomNavigationItemView(
    item: BottomNavItem,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Colores ajustados al tema
        )
        Text(
            text = item.label,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Colores ajustados al tema
        )
    }
}

// Clase de datos para representar un ítem de la barra de navegación
data class BottomNavItem(
    val label: String,
    val icon: Int,
    val isSelected: Boolean = false
)
