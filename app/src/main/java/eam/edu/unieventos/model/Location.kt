package eam.edu.unieventos.model


data class Location(
    var id: String = "",
    val name: String = "",
    val price: Float = 0.0f,
    val maxCapacity: Int = 0,
    val ticketsSold: Int = 0,
    var eventCode: String = "",
    val isActive: Boolean = false
) {
}