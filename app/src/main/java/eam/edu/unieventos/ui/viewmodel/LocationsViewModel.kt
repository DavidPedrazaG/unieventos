package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

class LocationsViewModel(private val context: Context) : ViewModel() {

    private val _locations = MutableStateFlow(emptyList<Location>())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    init {
        _locations.value = getLocationsList(context)
    }
    fun getLocationById(id: String): Location? {
        return _locations.value.find { it.id == id }
    }

    fun getLocationByName(name: String): Location? {
        return _locations.value.find { it.name == name }
    }

    fun getLocationsByEvent(eventCode: String): List<Location> {
        return _locations.value.filter { it.eventCode == eventCode }
    }


    fun createLocation(location: Location) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${location.id}_id", location.id)
        editor.putString("${location.id}_name", location.name)
        editor.putFloat("${location.id}_price", location.price)
        editor.putInt("${location.id}_maxCapacity", location.maxCapacity)
        editor.putInt("${location.id}_ticketsSold", location.ticketsSold)
        editor.putBoolean("${location.id}_isActive", location.isActive)
        editor.putString("${location.id}_eventId", location.eventCode)

        editor.putStringSet("stored_locations", (sharedPreferences.getStringSet("stored_locations", emptySet()) ?: emptySet()).plus(location.id))
        editor.apply()
    }

    fun validateLocation(id: String): Location? {
        return _locations.value.find { it.id == id && it.isActive }
    }

    fun updateLocation(location: Location) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${location.id}_name", location.name)
        editor.putFloat("${location.id}_price", location.price)
        editor.putInt("${location.id}_maxCapacity", location.maxCapacity)
        editor.putInt("${location.id}_ticketsSold", location.ticketsSold)
        editor.putBoolean("${location.id}_isActive", location.isActive)
        editor.putString("${location.id}_eventId", location.eventCode)

        editor.apply()
    }


    private fun getLocationsList(context: Context): List<Location> {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val storedLocations = mutableListOf<Location>()
        val storedIds = sharedPreferences.getStringSet("stored_locations", emptySet()) ?: emptySet()

        for (id in storedIds) {
            val name = sharedPreferences.getString("${id}_name", "") ?: ""
            val price = sharedPreferences.getFloat("${id}_price", 0f)
            val maxCapacity = sharedPreferences.getInt("${id}_maxCapacity", 0)
            val ticketsSold = sharedPreferences.getInt("${id}_ticketsSold", 0)
            val isActive = sharedPreferences.getBoolean("${id}_isActive", true)
            val eventId = sharedPreferences.getString("${id}_eventId", "") ?:""

            val location = Location(
                id = id,
                name = name,
                price = price,
                maxCapacity = maxCapacity,
                ticketsSold = ticketsSold,
                eventCode = eventId,
                isActive = isActive
            )
            storedLocations.add(location)
        }

        return storedLocations
    }



    fun printLocations() {
        val locationsList = _locations.value
        if (locationsList.isEmpty()) {
            Log.d("LocationsViewModel", "No hay localidades disponibles.")
        } else {
            for (location in locationsList) {
                Log.d("LocationsViewModel", "ID: ${location.id}, Name: ${location.name}, Price: ${location.price}, Max Capacity: ${location.maxCapacity}, Tickets Sold: ${location.ticketsSold}, Is Active: ${location.isActive}, Event ID: ${location.eventCode}")
            }
        }
    }


    fun deactivateLocation(locationId: String) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)

        // Verifica si existe la ubicación
        if (sharedPreferences.contains("${locationId}_id")) {
            // Actualiza el estado de la ubicación a inactiva
            val editor = sharedPreferences.edit()
            editor.putBoolean("${locationId}_isActive", false)
            editor.apply()

            // Actualiza la lista de ubicaciones en el ViewModel
            val updatedLocations = _locations.value.map {
                if (it.id == locationId) {
                    it.copy(isActive = false) // Crea una nueva instancia de Location con isActive en false
                } else {
                    it
                }
            }
            _locations.value = updatedLocations

            Log.i("LocationsViewModel", "Ubicación desactivada: $locationId")
        } else {
            Log.e("LocationsViewModel", "Ubicación no encontrada con ID: $locationId")
        }
    }

    fun activateLocation(locationId: String) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)

        // Verifica si existe la ubicación
        if (sharedPreferences.contains("${locationId}_id")) {
            // Actualiza el estado de la ubicación a activa
            val editor = sharedPreferences.edit()
            editor.putBoolean("${locationId}_isActive", true)
            editor.apply()

            // Actualiza la lista de ubicaciones en el ViewModel
            val updatedLocations = _locations.value.map {
                if (it.id == locationId) {
                    it.copy(isActive = true) // Crea una nueva instancia de Location con isActive en true
                } else {
                    it
                }
            }
            _locations.value = updatedLocations

            Log.i("LocationsViewModel", "Ubicación activada: $locationId")
        } else {
            Log.e("LocationsViewModel", "Ubicación no encontrada con ID: $locationId")
        }
    }


}
