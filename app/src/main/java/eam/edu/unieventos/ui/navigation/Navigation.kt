package eam.edu.unieventos.ui.navigation

import eam.edu.unieventos.ui.viewmodel.UsersViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eam.edu.unieventos.model.Role
import eam.edu.unieventos.ui.navigation.RouteScreen
import eam.edu.unieventos.ui.screens.EventDetailScreen
import eam.edu.unieventos.ui.screens.HomeScreen
import eam.edu.unieventos.ui.screens.LoginScreen
import eam.edu.unieventos.ui.screens.RecoveryScreen
import eam.edu.unieventos.ui.screens.RegisterScreen
import eam.edu.unieventos.ui.screens.UserConfigurationScreen
import eam.edu.unieventos.ui.screens.ValidationScreen
import eam.edu.unieventos.ui.viewmodel.ClientsViewModel

import eam.edu.unieventos.utils.SharedPreferenceUtils

@Composable
fun Navigation(

){

    var email: String = ""
    val navController = rememberNavController()
    val context = LocalContext.current

    var startDestination: RouteScreen = RouteScreen.Login
    val sesion = SharedPreferenceUtils.getCurrenUser(context)


    if(sesion != null){
        startDestination = if (sesion.rol == "admin") {
            RouteScreen.Home
        }else{
            RouteScreen.Home
        }

    }



    //Navhost es una funcion que importamos del compose, esta pide como parametros el controlador y el donde esta y hacia adonde va a ir
    NavHost(
        navController = navController, //Controlador
        startDestination = startDestination //Destino inicial
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
                onNavigateToValidate = { emailRecieved ->
                    email = emailRecieved
                    navController.navigate(RouteScreen.Validation)
                },
                onNavigateToHome = { role ->

                    val homeScreen = if (role == "admin") {
                        RouteScreen.Home // Navegar a la pantalla de inicio para admin
                    } else {
                        RouteScreen.Home // Navegar a la pantalla de inicio para cliente
                    }
                    navController.navigate(homeScreen)
                }
            )
        }
        //OJO es necesario crear el composable de la interfaz a la que queremos ir, sin esto no nos llevara a ningun lado
        composable<RouteScreen.Register>{
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(RouteScreen.Login)
                }
            )
        }
        composable<RouteScreen.Recovery> {
            RecoveryScreen(
                email = email,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<RouteScreen.Validation> {
            ValidationScreen(
                email = email,
                onValidationSuccess = {
                    navController.navigate(RouteScreen.Home)
                }
            )
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
                },
                onLogout = {
                    SharedPreferenceUtils.clearPreference(context)
                    navController.navigate(RouteScreen.Login)
                },
                onNavegateToUserConfig = {
                    navController.navigate(RouteScreen.UserConfig)
                }
            )
        }

        composable<RouteScreen.UserConfig> {
            UserConfigurationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}