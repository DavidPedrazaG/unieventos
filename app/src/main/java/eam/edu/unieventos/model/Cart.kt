package eam.edu.unieventos.model

public data class Cart (
    val id : String,
    val client: Client,
    val items:MutableList<Item> = mutableListOf()
)