package com.example.mobileappsproject.ui.navigation

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
}