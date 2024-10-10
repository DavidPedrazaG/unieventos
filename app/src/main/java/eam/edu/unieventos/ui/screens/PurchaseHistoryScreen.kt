package eam.edu.unieventos.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar

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
){
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
    ){ paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box{
                Text(text = "Historial de compras")
            }
        }
    }
}
