package eam.edu.unieventos.model

import java.util.Date

public data class Order (
    val id:String,
    val code: String,
    val clientId: String,
    var items: MutableList<String> = mutableListOf(),
    val usedCoupon: String,
    val totalAmount: Float,
    val purchaseDate : Date,
    val paymentDay: Date,
    val isActive: Boolean

)