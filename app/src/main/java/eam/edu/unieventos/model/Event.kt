package eam.edu.unieventos.model

import java.time.LocalTime
import java.util.Date
import java.time.format.DateTimeFormatter
data class Event(
    var id: String = "",
    val code: String = "",
    val name: String = "",
    val place: String = "",
    val city: String = "",
    val description: String = "",
    val type: String = "",
    val poster: String = "",
    val locationImage: String = "",
    //val locations: List<String> = listOf(),
    val dateEvent: Date = Date(),
    val time: String = "00:00",
    var isActive: Boolean = false
) {
}