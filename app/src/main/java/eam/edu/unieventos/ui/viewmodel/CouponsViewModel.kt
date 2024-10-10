package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CouponsViewModel(private val context: Context) : ViewModel() {

    private val _coupons = MutableStateFlow(emptyList<Coupon>())
    val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    init {
        _coupons.value = getCouponsList(context)
    }



    fun getCouponByCode(couponCode: String): Coupon? {
        val sharedPreferences = context.getSharedPreferences("CouponPrefs", Context.MODE_PRIVATE)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Verifica si el cupón existe en SharedPreferences
        if (sharedPreferences.contains("${couponCode}_id")) {
            val id = sharedPreferences.getString("${couponCode}_id", "") ?: ""
            val discountPercentage = sharedPreferences.getFloat("${couponCode}_discountPercentage", 0f)
            val expirationDateStr = sharedPreferences.getString("${couponCode}_expirationDate", "") ?: ""
            val expirationDate = dateFormat.parse(expirationDateStr) ?: Date()
            val eventCode = sharedPreferences.getString("${couponCode}_eventCode", null)
            val type = sharedPreferences.getInt("${couponCode}_type", 0)
            val isActive = sharedPreferences.getBoolean("${couponCode}_isActive", true)

            // Construye y devuelve el objeto Coupon
            return Coupon(
                id = id,
                code = couponCode,
                discountPercentage = discountPercentage,
                expirationDate = expirationDate,
                eventCode = eventCode,
                type = type,
                isActive = isActive
            )
        } else {
            // Si el cupón no se encuentra, retorna null
            Log.e("CouponsViewModel", "Cupón no encontrado con código: $couponCode")
            return null
        }
    }




    fun createCoupon(coupon: Coupon) {
        val sharedPreferences = context.getSharedPreferences("CouponPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editor.putString("${coupon.code}_id", coupon.id)
        editor.putString("${coupon.code}_code", coupon.code)
        editor.putFloat("${coupon.code}_discountPercentage", coupon.discountPercentage)
        editor.putString("${coupon.code}_expirationDate", dateFormat.format(coupon.expirationDate))
        editor.putString("${coupon.code}_eventCode", coupon.eventCode)
        editor.putInt("${coupon.code}_type", coupon.type)
        editor.putBoolean("${coupon.code}_isActive", coupon.isActive)

        editor.putStringSet("stored_coupons", (sharedPreferences.getStringSet("stored_coupons", emptySet()) ?: emptySet()).plus(coupon.code))
        editor.apply()
    }
///////////////////////////////////////// este hay que revisarlo bien
    fun validateCoupon(code: String): Coupon? {
        return _coupons.value.find { it.code == code && it.isActive }
    }
////////////////////////////////7
    fun updateCoupon(coupon: Coupon) {
        val sharedPreferences = context.getSharedPreferences("CouponPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editor.putString("${coupon.code}_id", coupon.id)
        editor.putFloat("${coupon.code}_discountPercentage", coupon.discountPercentage)
        editor.putString("${coupon.code}_expirationDate", dateFormat.format(coupon.expirationDate))
        editor.putString("${coupon.code}_eventCode", coupon.eventCode)
        editor.putInt("${coupon.code}_type", coupon.type)
        editor.putBoolean("${coupon.code}_isActive", coupon.isActive)

        editor.apply()
    }



    private fun getCouponsList(context: Context): List<Coupon> {
        val sharedPreferences = context.getSharedPreferences("CouponPrefs", Context.MODE_PRIVATE)
        val storedCoupons = mutableListOf<Coupon>()
        val storedCodes = sharedPreferences.getStringSet("stored_coupons", emptySet()) ?: emptySet()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (code in storedCodes) {
            val id = sharedPreferences.getString("${code}_id", "") ?: ""
            val discountPercentage = sharedPreferences.getFloat("${code}_discountPercentage", 0f)
            val expirationDateStr = sharedPreferences.getString("${code}_expirationDate", "") ?: ""
            val expirationDate = dateFormat.parse(expirationDateStr) ?: Date()
            val eventCode = sharedPreferences.getString("${code}_eventCode", null)
            val type = sharedPreferences.getInt("${code}_type",0)
            val isActive = sharedPreferences.getBoolean("${code}_isActive", true)

            val coupon = Coupon(
                id = id,
                code = code,
                discountPercentage = discountPercentage,
                expirationDate = expirationDate,
                eventCode = eventCode,
                type = type,
                isActive = isActive
            )
            storedCoupons.add(coupon)
        }

        return storedCoupons
    }

    fun generateCouponCode(): String {
        val prefix = "CUP"
        val timestamp = System.currentTimeMillis().toString()
        return "$prefix$timestamp"
    }


    fun generateCouponId(): String {
        val uniqueId = System.currentTimeMillis().toString() + (0..999).random().toString()
        return uniqueId
    }


    fun deactivateCoupon(code: String, usersViewModel: UsersViewModel) {
        val coupon = getCouponByCode(code)

        coupon?.let {
            // Desactiva el cupón globalmente
            it.isActive = false
            updateCoupon(it) // Actualiza el cupón en SharedPreferences
            Log.i("CouponsViewModel", "Cupón desactivado: ${it.code}")

            // Recorre todos los usuarios y elimina el cupón de su lista si lo tienen
            val client =
                usersViewModel.getClientsList(context) // Asume que esta función retorna todos los usuarios

            client.forEach { client ->
                if (client.availableCoupons.contains(code)) {
                    // Elimina el código del cupón de la lista de cupones del usuario
                    val updatedCoupons =
                        client.availableCoupons.toMutableList().apply { remove(code) }
                    client.availableCoupons = updatedCoupons

                    // Actualiza el usuario en SharedPreferences o la base de datos
                    usersViewModel.updateUser(client)

                    Log.i(
                        "CouponsViewModel",
                        "Cupón eliminado del usuario: ${client.name}, Cupón: $code"
                    )
                }
            }
        } ?: Log.e("CouponsViewModel", "Cupón no encontrado con código: $code")
    }

    fun deactivateExpiredCoupons(usersViewModel: UsersViewModel) {
        val currentDate = Date() // Obtiene la fecha actual
        val couponsList = getCouponsList(context) // Obtiene la lista de todos los cupones almacenados

        couponsList.forEach { coupon ->
            // Verifica si el cupón ya expiró
            if (coupon.expirationDate.before(currentDate) && coupon.isActive) {
                // Desactiva el cupón globalmente
                coupon.isActive = false
                updateCoupon(coupon) // Actualiza el cupón en SharedPreferences
                Log.i("CouponsViewModel", "Cupón desactivado por expiración: ${coupon.code}")

                // Recorre todos los usuarios y elimina el cupón de su lista si lo tienen
                val clients = usersViewModel.getClientsList(context) // Obtiene la lista de usuarios

                clients.forEach { client ->
                    if (client.availableCoupons.contains(coupon.code)) {
                        // Elimina el código del cupón de la lista de cupones del usuario
                        val updatedCoupons = client.availableCoupons.toMutableList().apply { remove(coupon.code) }
                        client.availableCoupons = updatedCoupons

                        // Actualiza el usuario en SharedPreferences o la base de datos
                        usersViewModel.updateUser(client)

                        Log.i("CouponsViewModel", "Cupón expirado eliminado del usuario: ${client.name}, Cupón: ${coupon.code}")
                    }
                }
            }
        }
    }


    fun printCoupons() {
        val couponsList = getCouponsList(context) // Obtiene la lista de todos los cupones
        Log.i("CouponsViewModel", "Imprimiendo todos los cupones:")

        couponsList.forEach { coupon ->
            Log.i(
                "CouponsViewModel",
                "ID: ${coupon.id}, Code: ${coupon.code}, Discount: ${coupon.discountPercentage}, " +
                        "Expiration Date: ${coupon.expirationDate}, Event Code: ${coupon.eventCode}, " +
                        "Type: ${coupon.type}, Is Active: ${coupon.isActive}"
            )
        }
    }



}
