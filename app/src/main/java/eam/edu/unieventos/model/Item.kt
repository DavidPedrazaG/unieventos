package eam.edu.unieventos.model

public data class Item (
    val id: String,
    val event: Event,
    val location: Location,
    val ticketQuantity: Int,
    val totalPrice: Float
)