package eam.edu.unieventos.model

import java.util.Date

data class Event(
    val id : String,
    val code: String,
    val name: String,
    val address: String,
    val city: String,
    val description: String,
    val type: String,
    val poster: String,
    val locationImage: String,
    val locations: List<Location>,
    val  dateEvent : Date
) {
}