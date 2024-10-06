package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Item
import eam.edu.unieventos.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderViewModel(private val context: Context) : ViewModel() {
    private val cartViewModel: CartViewModel = CartViewModel(context = context)
    private val _orders = MutableStateFlow(emptyList<Order>())
    val carts: StateFlow<List<Order>> = _orders.asStateFlow()
    init {
        _orders.value = getOrdersList(context)
    }

    fun addOrder(order: Order, context: Context, cart: Cart){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sharedPreferences = context.getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("${order.id}_id")) {
            val editor = sharedPreferences.edit()
            editor.putString("${order.id}_id", order.id)
            editor.putString("${order.id}_code", order.code)
            editor.putString("${order.id}_clientId", order.clientId)
            order.items=cartViewModel.loadItems(cart)
            editor.putStringSet("${order.id}_items", order.items.toSet())
            editor.putString("${order.id}_userCoupon", order.usedCoupon)
            editor.putFloat("${order.id}_totalAmount", order.totalAmount)
            editor.putLong("${order.id}_purchaseDate", order.purchaseDate.time)
            editor.putLong("${order.id}_paymentDay", order.paymentDay.time)
            editor.putBoolean("${order.code}_isActive", order.isActive)
            editor.putStringSet("stored_orders", (sharedPreferences.getStringSet("stored_orders", emptySet()) ?: emptySet()).plus(cart.id))
            editor.apply()
        }
    }

    private fun getOrdersList(context: Context): List<Order> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
        val storedOrders = mutableListOf<Order>()
        val storedIds = sharedPreferences.getStringSet("stored_orders", emptySet()) ?: emptySet()

        for (code in storedIds) {
            val id = sharedPreferences.getString("${code}_id", "") ?: ""
            val code = sharedPreferences.getString("${code}_code", "") ?: ""
            val clientId = sharedPreferences.getString("${code}_clientId", "")?: ""
            val itemsNull:MutableList<String> = mutableListOf()
            val items = sharedPreferences.getStringSet("${code}_items", itemsNull.toSet())?: itemsNull
            val usedCoupon = sharedPreferences.getString("${code}_userCoupon", "")
            val totalAmount = sharedPreferences.getFloat("${code}_totalAmount", 0f)
            val purchaseDate = sharedPreferences.getLong("${code}_purchaseDate", 0)
            val paymentDay = sharedPreferences.getLong("${code}_paymentDay", 0)
            val isActive = sharedPreferences.getBoolean("${code}_isActive", true)
            val order = Order(
                id = id,
                code = code,
                clientId = clientId,
                items = items as MutableList<String>,
                usedCoupon = usedCoupon ?: "",
                totalAmount = totalAmount,
                purchaseDate = Date(purchaseDate),
                paymentDay = Date(paymentDay),
                isActive = isActive
            )
            storedOrders.add(order)
        }

        return storedOrders
    }

    fun getOrderByCode(code: String): Order {
        return _orders.value.find { it.code == code }!!
    }

    fun getOrdersListByClient(clientID: String): List<Order> {
        return _orders.value.filter { it.clientId == clientID }
    }

}