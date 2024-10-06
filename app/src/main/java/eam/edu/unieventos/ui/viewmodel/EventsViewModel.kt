package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsViewModel(private val context: Context) : ViewModel() {

    private val _events = MutableStateFlow(emptyList<Event>())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        _events.value = getEventsList(context)
    }

    fun getEventById(id: String): Event? {
        return _events.value.find { it.id == id }
    }

    fun getEventByCode(code: String): Event? {
        return _events.value.find { it.code == code }
    }

    fun getEventByName(name: String): Event? {
        return _events.value.find { it.name == name }
    }

    fun getEventsByType(type: String): List<Event> {
        return _events.value.filter { it.type == type }
    }


    fun createEvent(event: Event) {
        val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editor.putString("${event.code}_id", event.id)
        editor.putString("${event.code}_code", event.code)
        editor.putString("${event.code}_name", event.name)
        editor.putString("${event.code}_address", event.address)
        editor.putString("${event.code}_city", event.city)
        editor.putString("${event.code}_description", event.description)
        editor.putString("${event.code}_type", event.type)
        editor.putString("${event.code}_poster", event.poster)
        editor.putString("${event.code}_locationImage", event.locationImage)
        editor.putLong("${event.code}_dateEvent", event.dateEvent.time)  // Guardar la fecha como timestamp

        editor.putStringSet("${event.code}_locations", event.locations.toSet())

        editor.putStringSet("stored_events", (sharedPreferences.getStringSet("stored_events", emptySet()) ?: emptySet()).plus(event.code))
        editor.apply()
    }


    fun updateEvent(event: Event) {
        val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${event.code}_name", event.name)
        editor.putString("${event.code}_address", event.address)
        editor.putString("${event.code}_city", event.city)
        editor.putString("${event.code}_description", event.description)
        editor.putString("${event.code}_type", event.type)
        editor.putString("${event.code}_poster", event.poster)
        editor.putString("${event.code}_locationImage", event.locationImage)
        editor.putLong("${event.code}_dateEvent", event.dateEvent.time)
        editor.putStringSet("${event.code}_locations", event.locations.toSet())

        editor.apply()
    }

    private fun getEventsList(context: Context): List<Event> {
        val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
        val storedEvents = mutableListOf<Event>()
        val storedCodes = sharedPreferences.getStringSet("stored_events", emptySet()) ?: emptySet()

        for (code in storedCodes) {
            val id = sharedPreferences.getString("${code}_id", "") ?: ""
            val name = sharedPreferences.getString("${code}_name", "") ?: ""
            val address = sharedPreferences.getString("${code}_address", "") ?: ""
            val city = sharedPreferences.getString("${code}_city", "") ?: ""
            val description = sharedPreferences.getString("${code}_description", "") ?: ""
            val type = sharedPreferences.getString("${code}_type", "") ?: ""
            val poster = sharedPreferences.getString("${code}_poster", "") ?: ""
            val locationImage = sharedPreferences.getString("${code}_locationImage", "") ?: ""
            val dateEventMillis = sharedPreferences.getLong("${code}_dateEvent", 0L)

            val locationsSet = sharedPreferences.getStringSet("${code}_locations", emptySet()) ?: emptySet()
            val locations = locationsSet.toList()

            val event = Event(
                id = id,
                code = code,
                name = name,
                address = address,
                city = city,
                description = description,
                type = type,
                poster = poster,
                locationImage = locationImage,
                locations = locations,
                dateEvent = Date(dateEventMillis)
            )
            storedEvents.add(event)
        }

        return storedEvents
    }
}
