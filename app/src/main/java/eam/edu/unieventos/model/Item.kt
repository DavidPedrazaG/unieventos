package eam.edu.unieventos.model

public data class Item (
    val id: String,
    val eventId: String,
    val locationId: String,
    val ticketQuantity: Int,
    val totalPrice: Float
)