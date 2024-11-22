package eam.edu.unieventos.model

import java.util.Date

data class Notification(
    var id: String = "", // Proporciona valores por defecto
    val from: String = "",
    val to: String = "",
    val message: String = "",
    val eventId: String = "",
    val sendDate: Date = Date(),
    val isRead: Boolean = false
) {
    // El constructor sin argumentos es automáticamente generado por Kotlin con valores por defecto
}

