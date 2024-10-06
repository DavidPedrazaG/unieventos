package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClientViewModel(private val context: Context) : ViewModel() {

    private val _clients = MutableStateFlow(emptyList<Client>())
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    init {
        _clients.value = getClientsList(context)
    }


    fun createClient(client: Client) {
        val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${client.id}_id", client.id)
        editor.putString("${client.id}_name", client.name)
        editor.putString("${client.id}_idCard", client.idCard)
        editor.putString("${client.id}_role", client.role)
        editor.putString("${client.id}_email", client.email)
        editor.putString("${client.id}_address", client.address)
        editor.putString("${client.id}_phoneNumber", client.phoneNumber)
        editor.putString("${client.id}_password", client.password)
        editor.putBoolean("${client.id}_isActive", client.isActive)
        editor.putString("${client.id}_userAppConfigId", client.userAppConfigId)


        editor.putString("${client.id}_availableCoupons", client.availableCoupons.joinToString(","))
        editor.putString("${client.id}_purchaseHistory", client.purchaseHistory.joinToString(","))
        editor.putString("${client.id}_notifications", client.notifications.joinToString(","))
        // Add function for add cart

        editor.putString("${client.id}_friends", client.friends.joinToString(","))


        editor.putStringSet(
            "stored_client_ids",
            (sharedPreferences.getStringSet("stored_client_ids", emptySet()) ?: emptySet()).plus(client.id)
        )
        editor.apply()
    }


    fun getClientById(id: String): Client? {
        return _clients.value.find { it.id == id }
    }


    private fun getClientsList(context: Context): List<Client> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
        val storedClients = mutableListOf<Client>()
        val storedClientIds = sharedPreferences.getStringSet("stored_client_ids", emptySet()) ?: emptySet()

        for (clientId in storedClientIds) {
            val name = sharedPreferences.getString("${clientId}_name", "") ?: ""
            val idCard = sharedPreferences.getString("${clientId}_idCard", "") ?: ""
            val role = sharedPreferences.getString("${clientId}_role", "") ?: "Client"
            val email = sharedPreferences.getString("${clientId}_email", "") ?: ""
            val address = sharedPreferences.getString("${clientId}_address", "") ?: ""
            val phoneNumber = sharedPreferences.getString("${clientId}_phoneNumber", "") ?: ""
            val password = sharedPreferences.getString("${clientId}_password", "") ?: ""
            val isActive = sharedPreferences.getBoolean("${clientId}_isActive", true)
            val userAppConfigId = sharedPreferences.getString("${clientId}_userAppConfigId", "") ?: ""
            val availableCoupons = sharedPreferences.getString("${clientId}_availableCoupons", "")?.split(",")?.map { it.trim() } ?: emptyList()
            val purchaseHistory = sharedPreferences.getString("${clientId}_purchaseHistory", "")?.split(",")?.map { it.trim() } ?: emptyList()
            val notifications = sharedPreferences.getString("${clientId}_notifications", "")?.split(",")?.map { it.trim() } ?: emptyList()
            val cartId = sharedPreferences.getString("${clientId}_cartId", "") ?: ""
            val friends = sharedPreferences.getString("${clientId}_friends", "")?.split(",")?.map { it.trim() } ?: emptyList()

            val client = Client(
                id = clientId,
                name = name,
                idCard = idCard,
                role = role,
                email = email,
                address = address,
                phoneNumber = phoneNumber,
                password = password,
                isActive = isActive,
                userAppConfigId = userAppConfigId,
                availableCoupons = availableCoupons,
                purchaseHistory = purchaseHistory,
                notifications = notifications,
                cartId = cartId,
                friends = friends
            )

            storedClients.add(client)
        }


        return storedClients + listOf(
            Client(
                id = "123",
                name = "Juan",
                idCard = "12345",
                role = "Client",
                email = "juancitof@gmail.com",
                phoneNumber = "311311",
                password = "123456",
                address = "Calle Falsa 123",
                isActive = true,
                userAppConfigId = "config1",
                availableCoupons = listOf("coupon1", "coupon2"),
                purchaseHistory = listOf("order1", "order2"),
                notifications = listOf("notification1", "notification2"),
                cartId = "cart_123",
                friends = listOf("client_456", "client_789")
            ),
            Client(
                id = "456",
                name = "Maria",
                idCard = "67890",
                role = "Client",
                email = "maria@gmail.com",
                phoneNumber = "311123",
                password = "654321",
                address = "Avenida Siempre Viva",
                isActive = true,
                userAppConfigId = "config2",
                availableCoupons = listOf("coupon3"),
                purchaseHistory = listOf("order3"),
                notifications = listOf("notification3"),
                cartId = "cart_456",
                friends = listOf("client_123")
            )
        )
    }
}
