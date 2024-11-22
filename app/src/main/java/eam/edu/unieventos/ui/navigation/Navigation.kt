package eam.edu.unieventos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eam.edu.unieventos.ui.screens.ActiveCouponsList
import eam.edu.unieventos.ui.screens.AddCoupon
import eam.edu.unieventos.ui.screens.HomeScreen
import eam.edu.unieventos.ui.screens.LoginScreen
import eam.edu.unieventos.ui.screens.NotificationsScreen
import eam.edu.unieventos.ui.screens.PurchaseHistoryScreen
import eam.edu.unieventos.ui.screens.RecoveryScreen
import eam.edu.unieventos.ui.screens.RegisterScreen
import eam.edu.unieventos.ui.screens.UserConfigurationScreen
import eam.edu.unieventos.ui.screens.ValidationScreen
import eam.edu.unieventos.ui.screens.SettingsScreen
import eam.edu.unieventos.ui.screens.AddEvent
import eam.edu.unieventos.ui.screens.EventDetails
import eam.edu.unieventos.ui.screens.EditEvent
import eam.edu.unieventos.ui.screens.PurchaseScreen
import eam.edu.unieventos.ui.screens.EditCoupon
import eam.edu.unieventos.ui.screens.FriendListScreen
import eam.edu.unieventos.ui.screens.FriendScreen

import eam.edu.unieventos.utils.SharedPreferenceUtils

@Composable
fun Navigation(

){

    var email: String = ""
    var code : String = ""
    var event: String = ""
    var coupon: String = ""
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
                onNavigateToValidate = { emailRecieved,generatedCode ->
                    email = emailRecieved
                    code = generatedCode
                    navController.navigate(RouteScreen.Validation)
                },
                onNavigateToHome = { role ->
                    navController.navigate(RouteScreen.Home)
                },
                onNavigateToFriendScreen = {
                    navController.navigate(RouteScreen.Friends)
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

        composable<RouteScreen.Home>{
            HomeScreen(
                onLogout = {
                    SharedPreferenceUtils.clearPreference(context)
                    navController.navigate(RouteScreen.Login)
                },
                onNavegateToUserConfig = {
                    navController.navigate(RouteScreen.UserConfig)
                },
                onNavegateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavegateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavegateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavegateToAddEvent = {
                    navController.navigate(RouteScreen.AddEvent)
                },
                onNavegateToAddCoupon = {
                    navController.navigate(RouteScreen.AddCoupon)
                },
                onNavegateToEventEdit = { eventRecived ->
                    event = eventRecived
                    navController.navigate(RouteScreen.EditEvent)

                },
                onNavegateToEventDetail = { eventRecived ->
                    event = eventRecived
                    navController.navigate(RouteScreen.EventDetail)
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                },
                onNavegateToPurchase = {
                    navController.navigate(RouteScreen.Purchase)
                },
                context = context
            )
        }

        composable<RouteScreen.AddEvent> {
            AddEvent(
                onBack = {
                    navController.popBackStack()
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                }
            )
        }

        composable<RouteScreen.EventDetail> {
            EventDetails(
                eventCode = event,
                onBack = {
                    navController.popBackStack()
                },
                onViewCart={
                    navController.navigate(RouteScreen.Purchase)
                }
            )
        }


        composable<RouteScreen.AddCoupon> {
            AddCoupon(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<RouteScreen.EditEvent> {
            EditEvent(
                eventCode = event,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<RouteScreen.Purchase>{
            PurchaseScreen(
                context = context,
                onNavegateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavegateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavegateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                },
                onNavegateToEventDetail = { eventRecived ->
                    event = eventRecived
                    navController.navigate(RouteScreen.EventDetail)
                }
            )
        }

        composable<RouteScreen.Settings>{
            SettingsScreen(
                context = context,
                onNavegateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavegateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavegateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                }
            )
        }

        composable<RouteScreen.EditCoupon> {
            EditCoupon(
                onClose = {
                    navController.popBackStack()
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                },
                couponCode = coupon

            )
        }

        composable<RouteScreen.Coupons> {
            ActiveCouponsList(
                context = context,
                onNavegateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavegateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavegateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                },
                onNavegateToEditCoupon = { couponRecived ->
                    coupon = couponRecived
                    navController.navigate(RouteScreen.EditCoupon)
                }
            )
        }

        composable<RouteScreen.PurchaseHistory> {
            PurchaseHistoryScreen(
                context = context,
                onNavegateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavegateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavegateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                }
            )
        }

        composable<RouteScreen.Notifications> {
            NotificationsScreen(
                context = context,
                onNavegateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavegateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavegateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                },
                onNavegateToFriendScreen = {
                    navController.navigate(RouteScreen.Friends)
                }
            )
        }

        composable<RouteScreen.UserConfig> {
            UserConfigurationScreen(

                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(RouteScreen.Login)
                }
            )
        }

        composable<RouteScreen.Friends> {
            FriendScreen(
                context = context,
                onNavegateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavegateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavegateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavegateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavegateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                },
                onNavegateToFriendList = {
                    navController.navigate(RouteScreen.FriendList)
                }
            )
        }

        composable<RouteScreen.FriendList>{
            FriendListScreen(
                context = context,
                onNavigateToSettings = {
                    navController.navigate(RouteScreen.Settings)
                },
                onNavigateToNotifications = {
                    navController.navigate(RouteScreen.Notifications)
                },
                onNavigateToPurchaseHistory = {
                    navController.navigate(RouteScreen.PurchaseHistory)
                },
                onNavigateToHome = {
                    navController.navigate(RouteScreen.Home)
                },
                onNavigateToCoupons = {
                    navController.navigate(RouteScreen.Coupons)
                }
            )
        }
    }
}

