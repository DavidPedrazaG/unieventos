package eam.edu.unieventos.model

import java.util.Date

data class Coupon (
    var id: String = "",
    val code: String = "",
    val discountPercentage: Float = 0.0f,
    val expirationDate: Date = Date(),
    val eventCode: String? = "",
    var type : Int = 1,  // 1 si es para un solo usuario ó 2 si es para variuos usuarios
    var isActive: Boolean = false

){
}