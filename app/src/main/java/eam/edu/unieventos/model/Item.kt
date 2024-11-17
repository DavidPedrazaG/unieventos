package eam.edu.unieventos.model

public data class Item (
    var id: String = "",
    val eventCode: String = "",
    val locationId: String = "",
    var ticketQuantity: Int = 0,
    var totalPrice: Float = 0f,
    var cartId: String = "",
    var orderCode: String = ""
)