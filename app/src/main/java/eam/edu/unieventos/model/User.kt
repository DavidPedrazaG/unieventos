package eam.edu.unieventos.model

open class User(
    val id: String,
    var name: String,
    var idCard: String,
    val role: String,
    var email: String,
    var address: String,
    var phoneNumber: String,
    var password: String,
    var isActive: Boolean ,
    var userAppConfigId: String,
    var isValidated: Boolean = false
) {

}