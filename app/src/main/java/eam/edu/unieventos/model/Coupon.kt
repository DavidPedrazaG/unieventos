package eam.edu.unieventos.model

import java.util.Date

data class Coupon (
    val id: String,
    val code: String,
    val discountPercentage: Float,
    val expirationDate: Date,
    val eventCode: String?,
    val type : Int,  // 1 si es para un solo usuario ó 2 si es para variuos usuarios
    var isActive: Boolean

){
}