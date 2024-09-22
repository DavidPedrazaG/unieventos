package eam.edu.unieventos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eam.edu.unieventos.ui.navigation.RouteScreen
import eam.edu.unieventos.ui.screens.EventDetailScreen
import eam.edu.unieventos.ui.screens.HomeScreen
import eam.edu.unieventos.ui.screens.LoginScreen
import eam.edu.unieventos.ui.screens.RecoveryScreen
import eam.edu.unieventos.ui.screens.RegisterScreen
import eam.edu.unieventos.ui.screens.ValidationScreen

@Composable
fun Navigation(){


    //Este es el controlador de navegaciones, con esto le indicaremos hacia que interfaz debe viajar
    val navController = rememberNavController()

    //Navhost es una funcion que importamos del compose, esta pide como parametros el controlador y el donde esta y hacia adonde va a ir
    NavHost(
        navController = navController, //Controlador
        startDestination = RouteScreen.Login //Destino inicial
    ){
        //Se crea un composable donde entre los <> se indica cual es la interfaz inicial, en este caso routescreen.login
        composable<RouteScreen.Login>{
            //Se llama el nombre de el archivo inicial, en este caso LoginScreen
            LoginScreen(
                //Se llama a la función que creamos adentro de LoginScreen, esta es la funcion que usaremos para redireccionarnos
                onNavigateToRegister = {
                    //Usando el controlador, le pasamos por el .navigate el route screen que creamos en la clase RouteScreen
                    navController.navigate(RouteScreen.Register)
                },
                onNavigateToRecovery = {
                    navController.navigate(RouteScreen.Recovery)
                },
                onNavigateToValidate = {
                    navController.navigate(RouteScreen.Validation)
                },
                onNavegateToHome = {
                    navController.navigate((RouteScreen.Home))
                }
            )
        }
        //OJO es necesario crear el composable de la interfaz a la que queremos ir, sin esto no nos llevara a ningun lado
        composable<RouteScreen.Register>{
            RegisterScreen()
        }
        composable<RouteScreen.Recovery> {
            RecoveryScreen()
        }
        composable<RouteScreen.Validation> {
            ValidationScreen()
        }
        composable<RouteScreen.Event> { 
            EventDetailScreen(
                eventName = "Evento",
                eventDate = "23/09/204",
                eventTime = "16:00",
                eventDescription = "Gran Evento",
                onBackClick = { /*TODO*/ }) {
                
            }
        }

        composable<RouteScreen.Home>{
            HomeScreen(
                onNavegateToEvent = {
                    navController.navigate((RouteScreen.Event))
                }
            )
        }
    }
}