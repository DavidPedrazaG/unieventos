package eam.edu.unieventos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel() : ViewModel() {

    val db = Firebase.firestore
    private val _carts = MutableStateFlow(emptyList<Cart>())
    val carts: StateFlow<List<Cart>> = _carts.asStateFlow()

    init {
        loadCarts()
    }

    fun loadCarts(){
        viewModelScope.launch {
            _carts.value = getCartsList()
        }
    }

    fun addCart(cart: Cart) {
        viewModelScope.launch {
            db.collection("carts")
                .add(cart)
                .await()
            loadCarts()
        }
    }

    private suspend fun getCartsList(): List<Cart> {
        val snapshot = db.collection("carts")
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val cart = it.toObject(Cart::class.java)
            requireNotNull(cart)
            cart.id = it.id
            cart
        }
    }

    fun addId(cart: Cart) {
        viewModelScope.launch {
            db.collection("carts")
                .document(cart.id)
                .set(cart)
                .await()
            loadCarts()
        }
    }

    fun updateCart(cart: Cart) {
        viewModelScope.launch {
            db.collection("carts")
                .document(cart.id)
                .set(cart)
                .await()

            _carts.value = getCartsList()
        }
    }

    fun getCartByClient(clientID: String): Cart? {
        return _carts.value.find { it.clientId == clientID }
    }

    fun getCartById(cartId: String): Cart? {
        return _carts.value.find { it.id == cartId }
    }
}