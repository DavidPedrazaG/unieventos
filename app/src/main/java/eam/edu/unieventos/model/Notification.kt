package eam.edu.unieventos.model

import java.util.Date

data class Notification(
    val id: String,
    val clientId: String,
    val message: String,
    val sendDate: Date,
    val isRead: Boolean
)

