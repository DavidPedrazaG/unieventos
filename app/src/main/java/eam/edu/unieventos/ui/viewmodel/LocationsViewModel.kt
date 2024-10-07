package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    fun getLocationsByEvent(eventId: String): List<Location> {
        return _locations.value.filter { it.eventId == eventId }
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
        editor.putString("${location.id}_eventId", location.eventId)

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
        editor.putString("${location.id}_eventId", location.eventId)

        editor.apply()
    }

    fun updateLocationStatus(id: String, isActive: Boolean) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("${id}_isActive", isActive)
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
            val eventId = sharedPreferences.getString("${id}_eventId", null)

            val location = Location(
                id = id,
                name = name,
                price = price,
                maxCapacity = maxCapacity,
                ticketsSold = ticketsSold,
                eventId = eventId,
                isActive = isActive
            )
            storedLocations.add(location)
        }

        return storedLocations
    }
}