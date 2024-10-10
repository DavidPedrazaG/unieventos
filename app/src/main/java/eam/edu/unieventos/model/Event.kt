package eam.edu.unieventos.model

import java.time.LocalTime
import java.util.Date

data class Event(
    val id : String,
    val code: String,
    val name: String,
    val place: String,
    val city: String,
    val description: String,
    val type: String,
    val poster: String,
    val locationImage: String,
    val locations: List<String>,
    val  dateEvent : Date,
    val time : LocalTime,
    var isActive: Boolean

) {
}