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

    fun addItem(cart: Cart? = null, context: Context, itemId: String){
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
        val item = itemViewModel.getItemById(context = context, itemId=itemId)
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
            val itemsNull:MutableList<String> = mutableListOf()
            val items = sharedPreferences.getStringSet("${code}_items", itemsNull.toSet())?: itemsNull

            val cart = Cart(
                id = id,
                clientId = clientId,
                items = items as MutableList<String>
            )
            storedCarts.add(cart)
        }

        return storedCarts
    }

    fun getCartByClient(context: Context, clientID: String): Cart? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val storedCarts = mutableListOf<Cart>()
        val storedIds = sharedPreferences.getStringSet("stored_carts", emptySet()) ?: emptySet()

        for (code in storedIds) {
            val clientId = sharedPreferences.getString("${code}_clientId", "")?: ""
                if (clientId == clientID) {
                    val id = sharedPreferences.getString("${code}_id", "") ?: ""
                    val itemsNull: MutableList<String> = mutableListOf()
                    val items = sharedPreferences.getStringSet("${code}_items", itemsNull.toSet())
                        ?: itemsNull

                    val cart = Cart(
                        id = id,
                        clientId = clientId,
                        items = items as MutableList<String>
                    )
                    return cart
                }
        }

        return null
    }
}