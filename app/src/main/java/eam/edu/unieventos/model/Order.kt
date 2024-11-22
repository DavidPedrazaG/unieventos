package eam.edu.unieventos.model

import java.util.Date

public data class Order (
    var id:String = "",
    var code: String = "",
    val clientId: String = "",
    //var items: MutableList<String> = mutableListOf(),
    val usedCoupon: String = "",
    val totalAmount: Float = 0.0f,
    val purchaseDate : Date = Date(),
    val paymentDay: Date = Date(),
    val isActive: Boolean = true

)