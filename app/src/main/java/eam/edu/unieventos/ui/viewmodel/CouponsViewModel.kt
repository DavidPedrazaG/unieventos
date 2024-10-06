package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CouponViewModel(private val context: Context) : ViewModel() {

    private val _coupons = MutableStateFlow(emptyList<Coupon>())
    val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    init {
        _coupons.value = getCouponsList(context)
    }

    fun getCouponById(id: String): Coupon? {
        return _coupons.value.find { it.id == id }
    }

    fun getCouponByCode(code: String): Coupon? {
        return _coupons.value.find { it.code == code }
    }

    fun getCouponsByEvent(eventId: String): List<Coupon> {
        return _coupons.value.filter { it.eventId == eventId }
    }


    fun createCoupon(coupon: Coupon) {
        val sharedPreferences = context.getSharedPreferences("CouponPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editor.putString("${coupon.code}_id", coupon.id)
        editor.putString("${coupon.code}_code", coupon.code)
        editor.putFloat("${coupon.code}_discountPercentage", coupon.discountPercentage)
        editor.putString("${coupon.code}_expirationDate", dateFormat.format(coupon.expirationDate))
        editor.putString("${coupon.code}_eventId", coupon.eventId)
        editor.putBoolean("${coupon.code}_isActive", coupon.isActive)

        editor.putStringSet("stored_coupons", (sharedPreferences.getStringSet("stored_coupons", emptySet()) ?: emptySet()).plus(coupon.code))
        editor.apply()
    }

    fun validateCoupon(code: String): Coupon? {
        return _coupons.value.find { it.code == code && it.isActive }
    }

    fun updateCoupon(coupon: Coupon) {
        val sharedPreferences = context.getSharedPreferences("CouponPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editor.putString("${coupon.code}_id", coupon.id)
        editor.putFloat("${coupon.code}_discountPercentage", coupon.discountPercentage)
        editor.putString("${coupon.code}_expirationDate", dateFormat.format(coupon.expirationDate))
        editor.putString("${coupon.code}_eventId", coupon.eventId)
        editor.putBoolean("${coupon.code}_isActive", coupon.isActive)

        editor.apply()
    }

    fun updateCouponStatus(code: String, isActive: Boolean) {
        val sharedPreferences = context.getSharedPreferences("CouponPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("${code}_isActive", isActive)
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
            val eventId = sharedPreferences.getString("${code}_eventId", null)
            val isActive = sharedPreferences.getBoolean("${code}_isActive", true)

            val coupon = Coupon(
                id = id,
                code = code,
                discountPercentage = discountPercentage,
                expirationDate = expirationDate,
                eventId = eventId,
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
}
