package eam.edu.unieventos.model

import java.util.Date

data class Notification(
    var id: String,
    val from: String,
    val to: String,
    val message: String,
    val eventId: String,
    val sendDate: Date,
    val isRead: Boolean
)

