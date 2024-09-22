package eam.edu.unieventos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eam.edu.unieventos.R
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar

@Composable
fun EventDetailScreen(
    modifier: Modifier = Modifier,
    eventName: String,
    eventDate: String,
    eventTime: String,
    eventDescription: String,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                selected = 0
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen del evento
            Image(
                painter = painterResource(id = R.drawable.placeholder_image), // Reemplaza con la imagen del evento
                contentDescription = "Event Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del evento
            Text(
                text = eventName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            // Fecha y Hora
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fecha",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = eventDate)
                }

                Column {
                    Text(
                        text = "Hora",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = eventTime)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = eventDescription,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Iconos de localidades
            Text(
                text = "Localidades",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_seat), // Reemplaza con el ícono de las sillas
                        contentDescription = "Seat",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de añadir al carrito
            Button(
                onClick = onAddToCartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart, // Reemplaza con el ícono del carrito
                    contentDescription = "Add to Cart",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "AÑADIR AL CARRO", fontSize = 16.sp)
            }
        }
    }
}
