package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Coupon
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
        deactivateEventByDate()
        deactivateCouponsByExpirationDate()
        viewModelScope.launch {
            _events.value = getEventsList()
        }
    }

    fun getEvents(): List<Event> {
        return _events.value
    }

    fun getEventsByType(type: String): List<Event> {
        return _events.value.filter { it.type == type }
    }

    fun getEventsByCity(city: String): List<Event> {
        return _events.value.filter { it.city == city }
    }

    fun getEventsByName(name: String): List<Event> {
        return _events.value.filter { it.name.contains(name, ignoreCase = true) }
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
            .whereEqualTo("active", true)
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val event = it.toObject(Event::class.java)
            requireNotNull(event)
            event.id = it.id
            event

        }
    }


    fun deactivateEvent(event: Event) {
        event.isActive = false
        viewModelScope.launch {
            // Desactiva los cupones asociados a este evento
            val couponsViewModel = CouponsViewModel() // Obtén una instancia del CouponsViewModel
            couponsViewModel.deactivateCouponByEventCode(event.code)

            // Desactiva el evento en la base de datos
            db.collection("events")
                .document(event.id)
                .set(event)
                .await()

            // Actualiza la lista de eventos activos
            _events.value = getEventsList()
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



    fun deactivateEventByDate() {
        viewModelScope.launch {
            val currentDateTime = Date().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime()

            val snapshot = db.collection("events").get().await()

            snapshot.documents.mapNotNull {
                val event = it.toObject(Event::class.java)
                event?.id = it.id
                event
            }.forEach { event ->
                try {
                    // Convertir la fecha del evento en LocalDate
                    val eventDate = event.dateEvent.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()

                    // Convertir la hora del evento en LocalTime
                    val eventHour = LocalTime.parse(event.time) // Formato "HH:mm"

                    // Crear la fecha y hora del evento (sin zona horaria)
                    val eventDateTime = eventDate.atTime(eventHour)

                    // Fecha 24 horas antes del evento
                    val twentyFourHoursBeforeEvent = eventDateTime.minusHours(48)

                    // Verificar si estamos dentro de las 24 horas previas al evento
                    if (currentDateTime.isAfter(eventDateTime)) {
                        // Si estamos después de la fecha y hora del evento, desactivar
                        deactivateEvent(event)
                        Log.d("EventsViewModel", "Evento desactivado: ${event.id}")
                    } else if (currentDateTime.isAfter(twentyFourHoursBeforeEvent)) {
                        // Si estamos dentro de las 24 horas previas al evento
                        deactivateEvent(event)
                        Log.d("EventsViewModel", "Evento desactivado: ${event.id}")
                    }
                } catch (e: Exception) {
                    Log.e("EventsViewModel", "Error al procesar evento: ${e.message}")
                }
            }

            // Actualizar la lista de eventos activos
            _events.value = getEventsList()
        }
    }

    fun deactivateCouponsByExpirationDate() {
        val couponsViewModel = CouponsViewModel() // Obtén la instancia del CouponsViewModel
        couponsViewModel.deactivateExpiredCoupons() // Desactiva los cupones expirados
    }



}
