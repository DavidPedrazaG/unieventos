package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CouponsViewModel() : ViewModel() {

    val db = Firebase.firestore
    private val _coupons = MutableStateFlow(emptyList<Coupon>())
    val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    init {
        loadCoupons()
    }


    fun loadCoupons() {
        viewModelScope.launch {
            _coupons.value = getCouponsList()
        }
    }


    private suspend fun getCouponsList(): List<Coupon> {
        val snapshot = db.collection("coupons")
            .whereEqualTo("active", true)
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            val coupon = it.toObject(Coupon::class.java)
            requireNotNull(coupon)
            coupon.id = it.id
            coupon
        }
    }


    fun createCoupon(coupon: Coupon) {
        viewModelScope.launch {
            db.collection("coupons")
                .add(coupon)
                .await()
            loadCoupons()
        }
    }


    fun updateCoupon(coupon: Coupon) {
        viewModelScope.launch {
            db.collection("coupons")
                .document(coupon.id)
                .set(coupon)
                .await()

            _coupons.value = getCouponsList()
        }
    }

    suspend fun getCouponByCode(code: String): Coupon? {
        val snapshot = db.collection("coupons")
            .whereEqualTo("code", code)
            .get()
            .await()

        return if (!snapshot.isEmpty) {
            val document = snapshot.documents.first()
            val coupon = document.toObject(Coupon::class.java)
            coupon?.id = document.id
            coupon
        } else {
            null
        }
    }

    suspend fun getCouponsByEventCode(eventCode: String): List<Coupon> {
        val snapshot = db.collection("coupons")
            .whereEqualTo("eventCode", eventCode)
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val coupon = it.toObject(Coupon::class.java)
            requireNotNull(coupon)
            coupon.id = it.id
            coupon
        }
    }



///////////////////////////////////////// este hay que revisarlo bien
    fun validateCoupon(code: String): Coupon? {
        return _coupons.value.find { it.code == code && it.isActive }
    }
////////////////////////////////
    fun generateCouponCode(): String {
        val prefix = "CUP"
        val timestamp = System.currentTimeMillis().toString()
        return "$prefix$timestamp"
    }


    fun deactivateCoupon(coupon: Coupon) {
        coupon.isActive = false
        viewModelScope.launch {
            db.collection("coupons")
                .document(coupon.id)
                .set(coupon)
                .await()

            _coupons.value = getCouponsList()
        }

    }






//    fun deactivateExpiredCoupons(usersViewModel: UsersViewModel) {
//        val currentDate = Date() // Obtiene la fecha actual
//        val couponsList = getCouponsList(context) // Obtiene la lista de todos los cupones almacenados
//
//        couponsList.forEach { coupon ->
//            // Verifica si el cupón ya expiró
//            if (coupon.expirationDate.before(currentDate) && coupon.isActive) {
//                // Desactiva el cupón globalmente
//                coupon.isActive = false
//                updateCoupon(coupon) // Actualiza el cupón en SharedPreferences
//                Log.i("CouponsViewModel", "Cupón desactivado por expiración: ${coupon.code}")
//
//                // Recorre todos los usuarios y elimina el cupón de su lista si lo tienen
//                val clients = usersViewModel.getClientsList(context) // Obtiene la lista de usuarios
//
//                clients.forEach { client ->
//                    if (client.availableCoupons.contains(coupon.code)) {
//                        // Elimina el código del cupón de la lista de cupones del usuario
//                        val updatedCoupons = client.availableCoupons.toMutableList().apply { remove(coupon.code) }
//                        client.availableCoupons = updatedCoupons
//
//                        // Actualiza el usuario en SharedPreferences o la base de datos
//                        usersViewModel.updateUser(client)
//
//                        Log.i("CouponsViewModel", "Cupón expirado eliminado del usuario: ${client.name}, Cupón: ${coupon.code}")
//                    }
//                }
//            }
//        }
//    }

}
