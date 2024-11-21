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
import androidx.compose.ui.res.stringResource
import eam.edu.unieventos.R


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
    val couponsViewModel: CouponsViewModel = remember { CouponsViewModel() }

    // Obtener la lista de cupones activos
    val activeCoupons = couponsViewModel.coupons.collectAsState().value
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
    ) { paddingValues ->
        // Se utiliza MaterialTheme para el tema y los colores
        val backgroundColor = MaterialTheme.colorScheme.background
        val textColor = MaterialTheme.colorScheme.onBackground

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.activeCoupons),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = textColor // Usamos el color de texto del tema
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar lista de cupones activos
            if (activeCoupons.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.noActiveCoupons),
                    color = textColor // Usamos el color de texto del tema
                )
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
    val textColor = MaterialTheme.colorScheme.onBackground // Usamos el color de texto del tema

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${stringResource(id = R.string.code)}: ${coupon.code}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor // Usamos el color de texto
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stringResource(id = R.string.discount)}: ${coupon.discountPercentage}%",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor // Usamos el color de texto
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stringResource(id = R.string.expiration)}: ${dateFormat.format(coupon.expirationDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor // Usamos el color de texto
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar el código del evento asociado o un mensaje si no está asociado
            if (coupon.eventCode != null) {
                Text(
                    text = "${stringResource(id = R.string.associatedEvent)}: ${coupon.eventCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor // Usamos el color de texto
                )
            } else {
                Text(
                    text = stringResource(id = R.string.notAssociated),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error // Usamos un color de error si no está asociado
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para editar el cupón
            Button(
                onClick = { onNavegateToEditCoupon(coupon.code) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(id = R.string.edit))
            }
        }
    }
}
