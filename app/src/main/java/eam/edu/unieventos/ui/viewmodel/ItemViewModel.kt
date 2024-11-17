package eam.edu.unieventos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ItemViewModel() : ViewModel(){

    val db = Firebase.firestore
    private val _items = MutableStateFlow(emptyList<Item>())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _items.value = getItemsList()
        }
    }

    fun generateRandomCode(length: Int): String {
        val charset = ('A'..'Z') + ('0'..'9')
        return List(length) { charset.random() }.joinToString("")
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            db.collection("items")
                .add(item)
                .await()
            loadItems()
        }

    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            db.collection("items")
                .document(item.id)
                .set(item)
                .await()

            _items.value = getItemsList()
        }
    }

    fun removeItem(itemId : String){
        viewModelScope.launch {
            db.collection("items")
                .document(itemId)
                .delete()
                .await()
            _items.value = getItemsList()
        }
    }


    private suspend fun getItemsList(): List<Item> {
        val snapshot = db.collection("items")
            .get()
            .await()
        return snapshot.documents.mapNotNull {
            val item = it.toObject(Item::class.java)
            requireNotNull(item)
            item.id = it.id
            item

        }
    }

    fun getItemById(itemId: String): Item? {
        return _items.value.find { it.id == itemId }
    }

    fun loadItemsByCart(cartId: String): List<Item> {
        return _items.value.filter { it.cartId == cartId }
    }

    fun loadItemsByOrder(orderCode: String): List<Item> {
        return _items.value.filter { it.orderCode == orderCode }
    }

    fun emptyCart(cartId: String, orderCode: String){
        val items = loadItemsByCart(cartId)
        items.forEach { item ->
            item.cartId = ""
            item.orderCode = orderCode
            updateItem(item)
        }

    }
}