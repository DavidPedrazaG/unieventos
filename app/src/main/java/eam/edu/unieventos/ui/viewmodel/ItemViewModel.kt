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
import eam.edu.unieventos.ui.viewmodel.CartViewModel

class ItemViewModel (private val context: Context) : ViewModel(){

    private val cartViewModel: CartViewModel = CartViewModel(context = context)
    private val _items = MutableStateFlow(emptyList<Item>())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    init {
        _items.value = getItemsList(context)
    }

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
            cartViewModel.addItem(context = context, itemId = item.id, cart = cart)
        } else {
            println("El item con id ${item.id} ya existe.")
        }
    }


    fun updateItem(item: Item, context: Context) {
        val sharedPreferences = context.getSharedPreferences("ItemPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("${item.id}_id", item.id)
        editor.putInt("${item.id}_ticketQuantity", item.ticketQuantity)
        editor.putFloat("${item.id}_totalPrice", item.totalPrice)
        editor.putString("${item.id}_event_id", item.eventId)
        editor.putString("${item.id}_location_id", item.locationId)
        editor.apply()
    }

    fun removeItem(item: Item, context: Context) {
        val sharedPreferences = context.getSharedPreferences("ItemPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("${item.id}_id")
        editor.remove("${item.id}_ticketQuantity")
        editor.remove("${item.id}_totalPrice")
        editor.remove("${item.id}_event_id")
        editor.remove("${item.id}_location_id")
        editor.apply() // Asegúrate de aplicar los cambios
    }


    private fun getItemsList(context: Context): List<Item> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("ItemPrefs", Context.MODE_PRIVATE)
        val storedItems = mutableListOf<Item>()
        val storedCodes = sharedPreferences.getStringSet("stored_codes", emptySet()) ?: emptySet()

        for (code in storedCodes) {
            val id = sharedPreferences.getString("${code}_id", "") ?: ""
            val ticketQuantity = sharedPreferences.getInt("${code}_ticketQuantity", 0)
            val totalPrice = sharedPreferences.getFloat("${code}_totalPrice", 0f)
            val eventId = sharedPreferences.getString("${code}_event_id", null) ?: ""
            val locationId = sharedPreferences.getString("${code}_location_id", null) ?: ""

            val item = Item(
                id = id,
                eventId = eventId,
                locationId = locationId,
                ticketQuantity = ticketQuantity,
                totalPrice = totalPrice
            )
            storedItems.add(item)
        }

        return storedItems
    }

    fun getItemById(itemId: String): Item? {
        return _items.value.find { it.id == itemId }
    }

}