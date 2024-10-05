package eam.edu.unieventos.model

import java.util.Date

public data class Order (
    val id:String,
    val code: String,
    val client: Client,
    val items: MutableList<Item> = mutableListOf(),
    val usedCoupon: Coupon,
    val totalAmount: Float,
    val purchaseDate : Date,
    val paymentDay: Date,
    val isActive: Boolean

)