package eam.edu.unieventos.model

public data class Cart (
    val id : String,
    val clientId: String,
    val items:MutableList<Item> = mutableListOf()
)