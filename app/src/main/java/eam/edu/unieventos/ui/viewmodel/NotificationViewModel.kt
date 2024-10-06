package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class NotificationViewModel(private val context: Context) : ViewModel() {

    private val _notifications = MutableStateFlow(emptyList<Notification>())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    init {
        _notifications.value = getNotificationsList(context)
    }

    fun createNotification(notification: Notification) {
        val sharedPreferences = context.getSharedPreferences("NotificationsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${notification.id}_message", notification.message)
        editor.putString("${notification.id}_clientId", notification.clientId)
        editor.putString("${notification.id}_sentDate", dateFormat.format(notification.sendDate)) // Convierte Date a String
        editor.putBoolean("${notification.id}_isRead", notification.isRead)

        editor.putStringSet(
            "stored_notifications",
            (sharedPreferences.getStringSet("stored_notifications", emptySet()) ?: emptySet()).plus(notification.id)
        )
        editor.apply()
    }

    fun getNotificationsList(context: Context): List<Notification> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("NotificationsPrefs", Context.MODE_PRIVATE)
        val storedNotifications = mutableListOf<Notification>()
        val storedNotificationIds = sharedPreferences.getStringSet("stored_notifications", emptySet()) ?: emptySet()

        for (id in storedNotificationIds) {
            val message = sharedPreferences.getString("${id}_message", "") ?: ""
            val clientId = sharedPreferences.getString("${id}_clientId", "") ?: ""
            val sentDateString = sharedPreferences.getString("${id}_sentDate", "") ?: ""
            val sentDate = if (sentDateString.isNotEmpty()) {
                dateFormat.parse(sentDateString) ?: Date()
            } else {
                Date()
            }
            val isRead = sharedPreferences.getBoolean("${id}_isRead", false)

            val notification = Notification(
                id = id,
                clientId = clientId,
                message = message,
                sendDate = sentDate,
                isRead = isRead
            )

            storedNotifications.add(notification)
        }

        return storedNotifications
    }

    fun markAsRead(notificationId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("NotificationsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("${notificationId}_isRead", true)
        editor.apply()

        _notifications.value = getNotificationsList(context)
    }
}
