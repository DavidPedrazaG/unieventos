package eam.edu.unieventos.model

data class User(
    val id: String,
    val name: String,
    val cc: String,
    val role: Role,
    val email: String,
    val cellphone: String,
    val password: String,
) {

}