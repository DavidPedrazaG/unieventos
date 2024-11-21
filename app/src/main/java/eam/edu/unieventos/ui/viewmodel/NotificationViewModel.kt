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
import eam.edu.unieventos.model.Event
import kotlinx.coroutines.tasks.await


class NotificationViewModel() : ViewModel() {

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

    fun getNotificationsByClient(clientId: String): List<Notification> {
        return _notifications.value.filter { it.to == clientId }
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

