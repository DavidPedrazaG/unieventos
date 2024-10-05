package eam.edu.unieventos.model

import java.util.Date

data class Coupon (
    val id: String,
    val code: String,
    val discountPercentage: Float,
    val expirationDate: Date,
    val event: Event?,
    val isActive: Boolean
){
}