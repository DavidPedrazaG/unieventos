package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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



    fun getEventsByType(type: String): List<Event> {
        return _events.value.filter { it.type == type }
    }

    fun generateRandomCode(length: Int): String {
        val charset = ('A'..'Z') + ('0'..'9')
        return List(length) { charset.random() }.joinToString("")
    }
    fun getEventById(eventId: String): Event? {
        return try {
            val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)

            // Verificar si existe el evento con el ID proporcionado
            if (sharedPreferences.contains("${eventId}_id")) {
                val id = sharedPreferences.getString("${eventId}_id", null)
                val code = sharedPreferences.getString("${eventId}_code", null)
                val name = sharedPreferences.getString("${eventId}_name", null)
                val address = sharedPreferences.getString("${eventId}_address", null)
                val city = sharedPreferences.getString("${eventId}_city", null)
                val description = sharedPreferences.getString("${eventId}_description", null)
                val type = sharedPreferences.getString("${eventId}_type", null)
                val poster = sharedPreferences.getString("${eventId}_poster", null)
                val locationImage = sharedPreferences.getString("${eventId}_locationImage", null)
                val dateEventMillis = sharedPreferences.getLong("${eventId}_dateEvent", 0L)
                val isActive = sharedPreferences.getBoolean("${eventId}_isActive", false)
                val locations = sharedPreferences.getStringSet("${eventId}_locations", emptySet())?.toList() ?: emptyList()

                // Verificar que los datos no sean nulos
                if (id != null && code != null && name != null && address != null && city != null && description != null && type != null && poster != null && locationImage != null && dateEventMillis != 0L) {
                    // Crear el objeto Event con los datos obtenidos
                    Event(
                        id = id,
                        code = code,
                        name = name,
                        place = address,
                        city = city,
                        description = description,
                        type = type,
                        poster = poster,
                        locationImage = locationImage,
                        locations = locations,
                        dateEvent = Date(dateEventMillis),
                        isActive = isActive
                    )
                } else {
                    null // Alguno de los campos no se encontró, retornar null
                }
            } else {
                null // El evento no existe
            }
        } catch (e: Exception) {
            Log.e("getEventById", "Error al obtener el evento: ${e.message}")
            e.printStackTrace()
            null // En caso de error, retornar null
        }
    }


    fun getEventByCode(code: String): Event? {
        return try {
            val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
            val storedIds = sharedPreferences.getStringSet("stored_code_events", emptySet()) ?: emptySet()

            for (eventId in storedIds) {
                val eventCode = sharedPreferences.getString("${eventId}_code", null)
                if (eventCode == code) {
                    return getEventById(eventId) // Reutiliza la función que busca por ID
                }
            }
            null // No se encontró el evento con el código proporcionado
        } catch (e: Exception) {
            Log.e("getEventByCode", "Error al obtener el evento: ${e.message}")
            e.printStackTrace()
            null
        }
    }




    fun createEvent(event: Event) {
        try {
            val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()


            editor.putString("${event.id}_id", event.id)
            editor.putString("${event.id}_code", event.code)
            editor.putString("${event.id}_name", event.name)
            editor.putString("${event.id}_address", event.place)
            editor.putString("${event.id}_city", event.city)
            editor.putString("${event.id}_description", event.description)
            editor.putString("${event.id}_type", event.type)
            editor.putString("${event.id}_poster", event.poster)
            editor.putString("${event.id}_locationImage", event.locationImage)
            editor.putLong("${event.id}_dateEvent", event.dateEvent.time)
            editor.putBoolean("${event.id}_isActive", event.isActive)

            editor.putStringSet("${event.id}_locations", event.locations.toSet())

            editor.putStringSet(
                "stored_code_events",
                (sharedPreferences.getStringSet("stored_code_events+", emptySet()) ?: emptySet()).plus(event.id)
            )
            editor.apply()
            Log.i("createEvent", "Evento guardado correctamente con ID: ${event.id} y código: ${event.code}")
        } catch (e: Exception) {
            Log.e("createEvent", "Error al guardar el evento: ${e.message}")
            e.printStackTrace()
        }
    }


    fun editEvent(event: Event) {
        try {
            val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("${event.id}_code", event.code)
            editor.putString("${event.id}_name", event.name)
            editor.putString("${event.id}_address", event.place)
            editor.putString("${event.id}_city", event.city)
            editor.putString("${event.id}_description", event.description)
            editor.putString("${event.id}_type", event.type)
            editor.putString("${event.id}_poster", event.poster)
            editor.putString("${event.id}_locationImage", event.locationImage)
            editor.putLong("${event.id}_dateEvent", event.dateEvent.time)
            editor.putBoolean("${event.id}_isActive", event.isActive)
            editor.putStringSet("${event.id}_locations", event.locations.toSet())

            editor.apply()
            //Log.i("editEvent", "Evento editado correctamente con ID: ${event.id}")
        } catch (e: Exception) {
            //Log.e("editEvent", "Error al editar el evento: ${e.message}")
            e.printStackTrace()
        }
    }


    fun deactivateEvent(eventId: String) {
        try {
            val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
            if (sharedPreferences.contains("${eventId}_id")) {
                val editor = sharedPreferences.edit()
                editor.putBoolean("${eventId}_isActive", false)
                editor.apply()
                Log.i("deactivateEvent", "Evento con ID: $eventId ha sido desactivado")
            } else {
                Log.e("deactivateEvent", "Evento con ID: $eventId no encontrado")
            }
        } catch (e: Exception) {
            Log.e("deactivateEvent", "Error al desactivar el evento: ${e.message}")
            e.printStackTrace()
        }
    }


    fun getEventByName(name: String): Event? {
        return try {
            val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
            val storedIds = sharedPreferences.getStringSet("stored_code_events", emptySet()) ?: emptySet()

            for (eventId in storedIds) {
                val eventName = sharedPreferences.getString("${eventId}_name", null)
                if (eventName.equals(name, ignoreCase = true)) {
                    return getEventById(eventId) // Reutiliza la función que busca por ID
                }
            }
            null // No se encontró el evento con el nombre proporcionado
        } catch (e: Exception) {
            Log.e("getEventByName", "Error al obtener el evento: ${e.message}")
            e.printStackTrace()
            null
        }
    }


    fun getAllEvents(): List<Event> {
        val eventsList = mutableListOf<Event>()
        return try {
            val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
            val storedIds = sharedPreferences.getStringSet("stored_code_events", emptySet()) ?: emptySet()

            for (eventId in storedIds) {
                val event = getEventById(eventId) // Reutiliza la función que busca por ID
                if (event != null) {
                    eventsList.add(event)
                }
            }
            eventsList
        } catch (e: Exception) {
            Log.e("getAllEvents", "Error al obtener los eventos: ${e.message}")
            e.printStackTrace()
            emptyList() // Retorna una lista vacía en caso de error
        }
    }







    private fun getEventsList(context: Context): List<Event> {
        val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
        val storedEvents = mutableListOf<Event>()
        val storedCodes = sharedPreferences.getStringSet("stored_events", emptySet()) ?: emptySet()
        Log.i("camilo","Antes del For")
        for (code in storedCodes) {
            val id = sharedPreferences.getString("${code}_id", "") ?: ""
            val name = sharedPreferences.getString("${code}_name", "") ?: ""
            val address = sharedPreferences.getString("${code}_address", "") ?: ""
            val city = sharedPreferences.getString("${code}_city", "") ?: ""
            val description = sharedPreferences.getString("${code}_description", "") ?: ""
            val type = sharedPreferences.getString("${code}_type", "") ?: ""
            val poster = sharedPreferences.getString("${code}_poster", "") ?: ""
            val locationImage = sharedPreferences.getString("${code}_locationImage", "") ?: ""
            val isActive = sharedPreferences.getBoolean("${code}_isActive", true)

            val dateEventMillis = sharedPreferences.getLong("${code}_dateEvent", 0L)

            val locationsSet = sharedPreferences.getStringSet("${code}_locations", emptySet()) ?: emptySet()
            val locations = locationsSet.toList()

            val event = Event(
                id = id,
                code = code,
                name = name,
                place = address,
                city = city,
                description = description,
                type = type,
                poster = poster,
                locationImage = locationImage,
                locations = locations,
                dateEvent = Date(dateEventMillis),
                isActive = isActive
            )
            Log.i("camilo",event.name)
            storedEvents.add(event)

        }


        return storedEvents
    }

    fun logAllEvents() {
        // Obtener los eventos almacenados
        val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
        val storedCodes = sharedPreferences.getStringSet("stored_code_events", emptySet()) ?: emptySet()

        // Verifica si hay eventos almacenados
        if (storedCodes.isEmpty()) {
            Log.i("logAllEvents", "No hay eventos almacenados.")
            return
        }

        // Itera sobre cada código de evento y recupera los datos
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

            // Convierte el timestamp a una fecha legible
            val dateEvent = if (dateEventMillis != 0L) {
                Date(dateEventMillis)
            } else {
                null
            }

            val locationsSet = sharedPreferences.getStringSet("${code}_locations", emptySet()) ?: emptySet()
            val locations = locationsSet.toList()

            // Imprime los detalles del evento en el log
            Log.i("logAllEvents", "----------------------------------")
            Log.i("logAllEvents", "ID: $id")
            Log.i("logAllEvents", "Código: $code")
            Log.i("logAllEvents", "Nombre: $name")
            Log.i("logAllEvents", "Dirección: $address")
            Log.i("logAllEvents", "Ciudad: $city")
            Log.i("logAllEvents", "Descripción: $description")
            Log.i("logAllEvents", "Tipo: $type")
            Log.i("logAllEvents", "Póster URL: $poster")
            Log.i("logAllEvents", "Imagen de Ubicación URL: $locationImage")
            Log.i("logAllEvents", "Fecha del Evento: ${dateEvent?.toString() ?: "No especificada"}")
            Log.i("logAllEvents", "Ubicaciones: ${locations.joinToString()}")
            Log.i("logAllEvents", "----------------------------------")
        }
    }

    fun deleteAllEvents() {
        try {
            // Inicializa SharedPreferences
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            // Obtener la lista de códigos de eventos almacenados
            val storedEvents: Set<String>? = sharedPreferences.getStringSet("stored_events", emptySet())

            // Si la lista no está vacía, eliminar todos los eventos
            storedEvents?.forEach { eventCode ->
                // Para cada código, obtener el ID y eliminar todas las entradas asociadas
                val eventId = sharedPreferences.getString("${eventCode}_id", null)
                if (eventId != null) {
                    editor.remove("${eventId}_id")
                    editor.remove("${eventId}_code")
                    editor.remove("${eventId}_name")
                    editor.remove("${eventId}_address")
                    editor.remove("${eventId}_city")
                    editor.remove("${eventId}_description")
                    editor.remove("${eventId}_type")
                    editor.remove("${eventId}_poster")
                    editor.remove("${eventId}_locationImage")
                    editor.remove("${eventId}_dateEvent")
                    editor.remove("${eventId}_locations")
                }
            }

            // Limpiar la lista general de eventos
            editor.remove("stored_events")

            // Aplicar los cambios
            editor.apply()

            Log.i("deleteAllEvents", "Todos los eventos han sido eliminados correctamente.")
        } catch (e: Exception) {
            Log.e("deleteAllEvents", "Error al eliminar los eventos: ${e.message}")
            e.printStackTrace()
        }
    }


}
