package eam.edu.unieventos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eam.edu.unieventos.ui.navigation.Navigation
import eam.edu.unieventos.ui.screens.EventDetailScreen
import eam.edu.unieventos.ui.theme.UniEventosTheme
import eam.edu.unieventos.ui.screens.HomeScreen
import eam.edu.unieventos.ui.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniEventosTheme {
                val usersViewModel = UsersViewModel(applicationContext)
                Navigation(usersViewModel = usersViewModel)
            }
        }
    }
}

/*@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UniEventosTheme {
        Greeting("Android")
    }
}*/