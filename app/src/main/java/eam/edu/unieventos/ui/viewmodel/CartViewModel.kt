package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel(private val context: Context) : ViewModel() {
    private val _carts = MutableStateFlow(emptyList<Cart>())
    val carts: StateFlow<List<Cart>> = _carts.asStateFlow()
    private val itemViewModel : ItemViewModel = ItemViewModel(context)
    init {
        _carts.value = getCartsList(context)
    }

    fun addCart(cart: Cart, context: Context){
        val sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("${cart.id}_id") && !sharedPreferences.contains("${cart.id}_clientId") ) {
            val editor = sharedPreferences.edit()
            editor.putString("${cart.id}_id", cart.id)
            editor.putString("${cart.id}_clientId", cart.clientId)
            editor.putStringSet("${cart.id}_items", cart.items.toSet())
            editor.putStringSet("stored_carts", (sharedPreferences.getStringSet("stored_carts", emptySet()) ?: emptySet()).plus(cart.id))
            editor.apply()
        } else {
            println("El carrito con id ${cart.id} ya existe.")
        }
    }

    fun loadItems(cart: Cart): MutableList<String>{
        val sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)

        val itemsSet = sharedPreferences.getStringSet("${cart.id}_items", emptySet()) ?: emptySet()
        cart.items.clear()
        cart.items.addAll(itemsSet)
        return cart.items
    }

    //REVISAR URGENTE!!!
    fun addItem(item: Item, context: Context, cart: Cart) {
        val sharedPreferences = context.getSharedPreferences("ItemPrefs", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("${item.id}_id")) {
            val editor = sharedPreferences.edit()
            editor.putString("${item.id}_id", item.id)
            editor.putInt("${item.id}_ticketQuantity", item.ticketQuantity)
            editor.putFloat("${item.id}_totalPrice", item.totalPrice)
            editor.putString("${item.id}_event_id", item.eventId)
            editor.putString("${item.id}_location_id", item.locationId)
            editor.putStringSet("stored_items", (sharedPreferences.getStringSet("stored_items", emptySet()) ?: emptySet()).plus(item.id))
            editor.apply()
            addItemToCart(context = context, itemId = item.id, cart = cart)
        } else {
            println("El item con id ${item.id} ya existe.")
        }
    }

    fun addItemToCart(cart: Cart, context: Context, itemId: String){
        val sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (cart!=null) {
            cart.items = loadItems(cart)
            cart.items.add(itemId)
            editor.putStringSet("${cart.id}_items", cart.items.toSet())
            editor.apply()
        }
    }

    fun removeItem(cart: Cart, context: Context, itemId: String){
        val sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        cart.items = loadItems(cart)
        cart.items.add(itemId)
        editor.putStringSet("${cart.id}_items", cart.items.toSet())
        editor.apply()
        val item = itemViewModel.getItemById(itemId=itemId)
        if (item != null) {
            itemViewModel.removeItem(context = context, item = item)
        }
    }

    private fun getCartsList(context: Context): List<Cart> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val storedCarts = mutableListOf<Cart>()
        val storedIds = sharedPreferences.getStringSet("stored_carts", emptySet()) ?: emptySet()

        for (code in storedIds) {
            val id = sharedPreferences.getString("${code}_id", "") ?: ""
            val clientId = sharedPreferences.getString("${code}_clientId", "")?: ""
            val itemsSet = sharedPreferences.getStringSet("${code}_items", emptySet()) ?: emptySet()
            val items:MutableList<String> = mutableListOf()
            items.addAll(itemsSet)
            val cart = Cart(
                id = id,
                clientId = clientId,
                items = items
            )
            storedCarts.add(cart)
        }

        return storedCarts
    }

    fun getCartByClient(clientID: String): Cart? {
        return _carts.value.find { it.clientId == clientID }
    }
}