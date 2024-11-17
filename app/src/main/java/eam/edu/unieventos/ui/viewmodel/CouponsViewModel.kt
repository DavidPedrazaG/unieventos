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

    fun redeemCoupon(couponCode: String){
        var coupon = validateCoupon(couponCode)
        if(coupon != null){
            coupon.isActive = false
            viewModelScope.launch {
                db.collection("coupons")
                    .document(coupon.id)

                    .set(coupon)
                    .await()

                _coupons.value = getCouponsList()
            }
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

    fun deactivateCouponByEventCode(eventCode: String) {
        viewModelScope.launch {
            // Obtén todos los cupones con el eventCode correspondiente
            val couponsList = getCouponsByEventCode(eventCode)

            // Desactiva cada cupón
            couponsList.forEach { coupon ->
                coupon.isActive = false
                updateCoupon(coupon) // Actualiza el estado del cupón en la base de datos
                Log.d("CouponsViewModel", "Cupón desactivado: ${coupon.code}")
            }

            // Recarga los cupones
            loadCoupons()
        }
    }



    fun deactivateExpiredCoupons() {
        viewModelScope.launch {
            val currentDateTime = Date()

            // Obtén todos los cupones
            val snapshot = db.collection("coupons")
                .whereEqualTo("active", true)  // Solo cupones activos
                .get()
                .await()

            // Recorre cada cupón y desactívalo si su fecha de expiración ha pasado
            snapshot.documents.mapNotNull { document ->
                val coupon = document.toObject(Coupon::class.java)
                coupon?.id = document.id
                coupon
            }.forEach { coupon ->
                // Verifica si la fecha de expiración ha pasado
                if (coupon.expirationDate.before(currentDateTime)) {
                    coupon.isActive = false // Desactiva el cupón
                    // Actualiza el cupón en la base de datos
                    db.collection("coupons")
                        .document(coupon.id)
                        .set(coupon)
                        .await()
                    Log.d("CouponsViewModel", "Cupón desactivado: ${coupon.id}")
                }
            }
        }
    }






}
