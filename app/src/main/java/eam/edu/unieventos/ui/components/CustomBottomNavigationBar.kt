package eam.edu.unieventos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R


@Composable
fun CustomBottomNavigationBar(
    modifier: Modifier = Modifier,
    selected: Int
) {

    var selectedItem by remember { mutableStateOf(selected) }

    val items = listOf(
        BottomNavItem("Inicio", R.drawable.ic_home, isSelected = selectedItem == 0),
        BottomNavItem("Historial", R.drawable.ic_history, isSelected = selectedItem == 1),
        BottomNavItem("Configuración", R.drawable.ic_settings, isSelected = selectedItem == 2),
        BottomNavItem("Notificaciones", R.drawable.ic_notifications, isSelected = selectedItem == 3)
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0x6200ee)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            var index = 0
            BottomNavigationItemView(
                item = item,
                onClick = { 0 },
                isSelected = item.isSelected
            )
            index =+1
        }
    }
}

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
            tint = if (isSelected) Color.White else Color.LightGray
        )
        Text(
            text = item.label,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color.LightGray
        )
    }
}

// Clase de datos para representar un ítem de la barra de navegación
data class BottomNavItem(
    val label: String,
    val icon: Int,
    val isSelected: Boolean = false
)