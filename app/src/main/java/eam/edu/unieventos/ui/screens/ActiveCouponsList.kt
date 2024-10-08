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
import androidx.compose.ui.unit.dp
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar
import eam.edu.unieventos.ui.viewmodel.CouponsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActiveCouponsList(
    modifier: Modifier = Modifier,
    context: Context,
    onNavegateToSettings: () -> Unit,
    onNavegateToNotifications: () -> Unit,
    onNavegateToPurchaseHistory: () -> Unit,
    onNavegateToHome: () -> Unit,
    onNavegateToCoupons: () -> Unit,
    onNavegateToEditCoupon: (String) -> Unit
) {
    val context = LocalContext.current
    val couponsViewModel: CouponsViewModel = remember { CouponsViewModel(context) }

    // Obtener la lista de cupones activos
    val activeCoupons = couponsViewModel.coupons.collectAsState().value.filter { it.isActive }
    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                modifier = Modifier,
                selected = 2,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Cupones Activos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar lista de cupones activos
            if (activeCoupons.isEmpty()) {
                Text(text = "No hay cupones activos en este momento.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activeCoupons) { coupon ->
                        CouponItem(coupon = coupon, onNavegateToEditCoupon = onNavegateToEditCoupon)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        }
}

@Composable
fun CouponItem(coupon: Coupon, onNavegateToEditCoupon: (String) -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Código: ${coupon.code}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Descuento: ${coupon.discountPercentage}%", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Expira el: ${dateFormat.format(coupon.expirationDate)}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar el código del evento asociado o un mensaje si no está asociado
            if (coupon.eventCode != null) {
                Text(text = "Evento Asociado: ${coupon.eventCode}", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(text = "No asociado a ningún evento", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para editar el cupón
            Button(
                onClick = { onNavegateToEditCoupon(coupon.code) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Editar")
            }
        }
    }
}
