package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class NotificationViewModel(private val context: Context) : ViewModel() {

    val db = Firebase.firestore
    private val _notifications = MutableStateFlow(emptyList<Notification>())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    init {
        loadNotification()
    }

    fun loadNotification() {
        viewModelScope.launch {
            _notifications.value = getNotificationsList()

        }
    }

    fun createNotification(notification: Notification) {
        viewModelScope.launch {
            db.collection("notifications")
                .add(notification)
                .await()
            loadNotification()
        }
    }

    /*
    fun createNotification(notification: Notification) {
        val sharedPreferences = context.getSharedPreferences("NotificationsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${notification.id}_message", notification.message)
        editor.putString("${notification.id}_from", notification.from)
        editor.putString("${notification.id}_to", notification.to)
        editor.putString("${notification.id}_eventId", notification.eventId)
        editor.putString("${notification.id}_sentDate", dateFormat.format(notification.sendDate))
        editor.putBoolean("${notification.id}_isRead", notification.isRead)

        editor.putStringSet(
            "stored_notifications",
            (sharedPreferences.getStringSet("stored_notifications", emptySet()) ?: emptySet()).plus(notification.id)
        )
        editor.apply()
    }

     */

    open suspend fun getNotificationsList(): List<Notification> {

        val snapshot = db.collection("notifications")
            .get()
            .await()


        val notifications = snapshot.documents.mapNotNull {
            val notification = it.toObject(Notification::class.java)
            requireNotNull(notification)
            notification.id = it.id
            notification
        }.toMutableList()

        return notifications
    }

    /*
    fun getNotificationsList(context: Context): List<Notification> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("NotificationsPrefs", Context.MODE_PRIVATE)
        val storedNotifications = mutableListOf<Notification>()
        val storedNotificationIds = sharedPreferences.getStringSet("stored_notifications", emptySet()) ?: emptySet()

        for (id in storedNotificationIds) {
            val message = sharedPreferences.getString("${id}_message", "") ?: ""
            val from = sharedPreferences.getString("${id}_from", "") ?: ""
            val to = sharedPreferences.getString("${id}_to", "") ?: "" // Recupera 'to'
            val eventId = sharedPreferences.getString("${id}_eventId", "") ?: ""
            val sentDateString = sharedPreferences.getString("${id}_sentDate", "") ?: ""
            val sentDate = if (sentDateString.isNotEmpty()) {
                dateFormat.parse(sentDateString) ?: Date()
            } else {
                Date()
            }
            val isRead = sharedPreferences.getBoolean("${id}_isRead", false)

            val notification = Notification(
                id = id,
                from = from,
                to = to, // Establece 'to'
                message = message,
                eventId = eventId,
                sendDate = sentDate,
                isRead = isRead
            )

            storedNotifications.add(notification)
        }

        return storedNotifications
    }

     */

    /*
    fun markAsRead(notificationId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("NotificationsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("${notificationId}_isRead", true)
        editor.apply()

        _notifications.value = getNotificationsList(context)
    }

     */
    fun generateNotificationId(): String {
        val uniqueId = System.currentTimeMillis().toString() + (0..999).random().toString()
        return uniqueId
    }


    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {

                val notificationRef = db.collection("notifications").document(notificationId)
                notificationRef.update("isRead", true).await()


                loadNotification()
            } catch (e: Exception) {
                println("Error marking notification as read: ${e.message}")
            }
        }
    }

}

