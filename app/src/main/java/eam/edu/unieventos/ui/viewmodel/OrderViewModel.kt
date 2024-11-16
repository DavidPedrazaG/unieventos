package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Cart
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
        order.id = generateRandomCode(6)
        order.code = generateRandomCode(8)
        viewModelScope.launch {
            db.collection("orders")
                .add(order)
                .await()
            loadOrders()
        }
        return order.id
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