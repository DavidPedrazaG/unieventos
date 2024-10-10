package eam.edu.unieventos.ui.navigation

import kotlinx.serialization.Serializable

sealed class RouteScreen {

    //Aqui creamos varios objetos de tipo RouteScreen, le ponemos el nombre coherente a la interfaz a la que vamos a ir

    @Serializable
    data object Login: RouteScreen()

    @Serializable
    data object Register: RouteScreen()

    @Serializable
    data object Recovery: RouteScreen()

    @Serializable
    data object Validation: RouteScreen()

    @Serializable
    data object Home: RouteScreen()

    @Serializable
    data object Event: RouteScreen()

    @Serializable
    data object UserConfig: RouteScreen()

    @Serializable
    data object PurchaseHistory: RouteScreen()

    @Serializable
    data object Notifications: RouteScreen()

    @Serializable
    data object Settings: RouteScreen()

    @Serializable
    data object AddEvent: RouteScreen()

    @Serializable
    data object AddCoupon: RouteScreen()

    @Serializable
    data object EventDetail: RouteScreen()

    @Serializable
    data object Coupons: RouteScreen()

    @Serializable
    data object EditCoupon: RouteScreen()

    @Serializable
    data object EditEvent: RouteScreen()

    @Serializable
    data object Purchase: RouteScreen()

}