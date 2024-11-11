package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import java.util.Date



class EventsViewModel() : ViewModel() {

    val db = Firebase.firestore
    private val _events = MutableStateFlow(emptyList<Event>())
    val events: StateFlow<List<Event>> = _events.asStateFlow()


    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _events.value = getEventsList()
        }
    }


    fun getEventsByType(type: String): List<Event> {
        return _events.value.filter { it.type == type }
    }

    fun generateRandomCode(length: Int): String {
        val charset = ('A'..'Z') + ('0'..'9')
        return List(length) { charset.random() }.joinToString("")
    }


    suspend fun getEventById(eventId: String): Event? {
        val snapshot = db.collection("events")
            .document(eventId)
            .get()
            .await()

        val event =snapshot.toObject(Event::class.java)
        event?.id = snapshot.id
        return  event

    }

    suspend fun getEventByCode(eventCode: String): Event? {
        val snapshot = db.collection("events")
            .whereEqualTo("code", eventCode)
            .get()
            .await()

        return if (!snapshot.isEmpty) {
            val document = snapshot.documents.first()
            val event = document.toObject(Event::class.java)
            event?.id = document.id
            event
        } else {
            null
        }
    }

    fun createEvent(event: Event) {
        viewModelScope.launch {
            db.collection("events")
                .add(event)
                .await()
            loadEvents()
        }

    }


    fun editEvent(event: Event) {
        viewModelScope.launch {
            db.collection("events")
                .document(event.id)
                .set(event)
                .await()

            _events.value = getEventsList()
        }
    }


    private suspend fun getEventsList(): List<Event> {
        val snapshot = db.collection("events")
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val event = it.toObject(Event::class.java)
            requireNotNull(event)
            event.id = it.id
            event

        }
    }

    fun deleteEvent(eventId : String){
        viewModelScope.launch {
            db.collection("events")
                .document(eventId)
                .delete()
                .await()
            _events.value = getEventsList()
        }
    }
}
