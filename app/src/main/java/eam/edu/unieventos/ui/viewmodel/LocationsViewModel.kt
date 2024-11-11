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
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationsViewModel() : ViewModel() {

    val db = Firebase.firestore
    private val _locations = MutableStateFlow(emptyList<Location>())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    init {
        loadLocations()
    }

    fun loadLocations() {
        viewModelScope.launch {
            _locations.value = getLocationsList()
        }
    }

    private suspend fun getLocationsList(): List<Location> {
        val snapshot = db.collection("locations")
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val location = it.toObject(Location::class.java)
            requireNotNull(location)
            location.id = it.id
            location

        }
    }

    fun loadLocationsByEventCode(eventCode: String) {
        viewModelScope.launch {
            _locations.value = getLocationsByEventCode(eventCode)
        }
    }

     suspend fun getLocationsByEventCode(eventCode: String): List<Location> {
        val snapshot = db.collection("locations")
            .whereEqualTo("eventCode", eventCode)
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val location = it.toObject(Location::class.java)
            requireNotNull(location)
            location.id = it.id
            location
        }
    }





    fun getLocationById(id: String): Location? {
        return _locations.value.find { it.id == id }
    }

    fun getLocationByName(name: String): Location? {
        return _locations.value.find { it.name == name }
    }


    fun createLocation(location: Location) {
        viewModelScope.launch {
            db.collection("locations")
                .add(location)
                .await()
        }

    }


    fun validateLocation(id: String): Location? {
        return _locations.value.find { it.id == id && it.isActive }
    }



    fun updateLocation(location: Location) {
        viewModelScope.launch {
            db.collection("locations")
                .document(location.id)
                .set(location)
                .await()

            _locations.value = getLocationsList()
        }
    }




//    fun deactivateLocation(locationId: String) {
//        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
//
//        // Verifica si existe la ubicación
//        if (sharedPreferences.contains("${locationId}_id")) {
//            // Actualiza el estado de la ubicación a inactiva
//            val editor = sharedPreferences.edit()
//            editor.putBoolean("${locationId}_isActive", false)
//            editor.apply()
//
//            // Actualiza la lista de ubicaciones en el ViewModel
//            val updatedLocations = _locations.value.map {
//                if (it.id == locationId) {
//                    it.copy(isActive = false) // Crea una nueva instancia de Location con isActive en false
//                } else {
//                    it
//                }
//            }
//            _locations.value = updatedLocations
//
//            Log.i("LocationsViewModel", "Ubicación desactivada: $locationId")
//        } else {
//            Log.e("LocationsViewModel", "Ubicación no encontrada con ID: $locationId")
//        }
//    }

//    fun activateLocation(locationId: String) {
//        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
//
//        // Verifica si existe la ubicación
//        if (sharedPreferences.contains("${locationId}_id")) {
//            // Actualiza el estado de la ubicación a activa
//            val editor = sharedPreferences.edit()
//            editor.putBoolean("${locationId}_isActive", true)
//            editor.apply()
//
//            // Actualiza la lista de ubicaciones en el ViewModel
//            val updatedLocations = _locations.value.map {
//                if (it.id == locationId) {
//                    it.copy(isActive = true) // Crea una nueva instancia de Location con isActive en true
//                } else {
//                    it
//                }
//            }
//            _locations.value = updatedLocations
//
//            Log.i("LocationsViewModel", "Ubicación activada: $locationId")
//        } else {
//            Log.e("LocationsViewModel", "Ubicación no encontrada con ID: $locationId")
//        }
//    }


}
