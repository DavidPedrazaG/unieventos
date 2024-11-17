package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Item
import eam.edu.unieventos.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderViewModel() : ViewModel() {

    val db = Firebase.firestore
    private val _orders = MutableStateFlow(emptyList<Order>())
    val carts: StateFlow<List<Order>> = _orders.asStateFlow()
    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _orders.value = getOrdersList()
        }
    }

    fun generateRandomCode(length: Int): String {
        val charset = ('A'..'Z') + ('0'..'9')
        return List(length) { charset.random() }.joinToString("")
    }

    fun addOrder(order: Order): String{
        order.code = generateRandomCode(6)
        viewModelScope.launch {
            db.collection("orders")
                .add(order)
                .await()
            loadOrders()
        }
        return order.code
    }

    fun validateCoupon(coupon: Coupon, clientId: String, cartItems: List<Item>, cart: Cart) : Cart{
        var unused = true
        var orders = getOrdersListByClient(clientId)
        for(order in orders){
            if(order.usedCoupon == coupon.code){
                unused = false
                break
            }
        }
        if(unused){
            if(coupon.eventCode != null){
                var discountAmount = 0f
                for(item in cartItems!!){
                    if(item.eventCode == coupon.eventCode){
                        discountAmount.plus(item.totalPrice)
                    }
                }
                discountAmount = discountAmount.minus(discountAmount * (coupon.discountPercentage / 100))
                cart.total = cart.total.minus(discountAmount)
            }else{
                cart.total = cart.total.minus(cart.total!! * (coupon.discountPercentage / 100))
            }
        }

        return cart
    }

    private suspend fun getOrdersList(): List<Order> {
        val snapshot = db.collection("orders")
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val order = it.toObject(Order::class.java)
            requireNotNull(order)
            order.id = it.id
            order

        }
    }

    fun getOrderByCode(code: String): Order {
        return _orders.value.find { it.code == code }!!
    }

    fun getOrdersListByClient(clientID: String): List<Order> {
        return _orders.value.filter { it.clientId == clientID }
    }

}