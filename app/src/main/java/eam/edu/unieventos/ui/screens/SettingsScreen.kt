package eam.edu.unieventos.ui.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eam.edu.unieventos.MainActivity
import eam.edu.unieventos.ui.components.CustomBottomNavigationBar
import java.util.Locale

fun setLocale(languageCode: String, context: Context) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)

    // Actualiza la configuración del recurso
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun toggleLanguage(context: Context, selectedLanguage: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // Guardar el nuevo idioma en SharedPreferences
    sharedPreferences.edit().putString("selected_language", selectedLanguage).apply()

    // Actualizar la configuración de idioma
    setLocale(selectedLanguage, context)

    // Recargar la actividad para aplicar los cambios
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onNavegateToSettings: () -> Unit,
    onNavegateToNotifications: () -> Unit,
    onNavegateToPurchaseHistory: () -> Unit,
    onNavegateToHome: () -> Unit,
    onNavegateToCoupons: () -> Unit
) {
    val languages = listOf("en", "es", "pt", "de")
    val currentLocale: Locale = Locale.getDefault()
    val currentLanguage: String = currentLocale.language
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                modifier = Modifier,
                selected = 3,
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
                Text(text = "Configuración")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Corregimos la llamada a DropdownMenu
            DropdownMenu(
                value = selectedLanguage,
                onValeChange = { language ->
                    selectedLanguage = language // Actualizamos el estado localmente
                    toggleLanguage(context, language) // Aplicamos el cambio de idioma
                },
                items = languages
            )
        }
    }
}
