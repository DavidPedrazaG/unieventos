package eam.edu.unieventos.model


data class Location(
    val id: String,
    val name: String,
    val price: Float,
    val maxCapacity: Int,
    val ticketsSold: Int,
    var eventId: String?,
    val isActive: Boolean
) {
}