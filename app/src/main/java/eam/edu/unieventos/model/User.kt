package eam.edu.unieventos.model

open class User(
    val id: String,
    val name: String,
    val idCard: String,
    val role: String,
    val email: String,
    val address: String,
    val phoneNumber: String,
    var password: String,
    var isActive: Boolean,
    var userAppConfigId: String,
    var isValidated: Boolean = false
) {

}