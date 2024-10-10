package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class UsersViewModel(protected val context: Context) : ViewModel() {

    protected val _clients = MutableStateFlow(emptyList<Client>())
    protected val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    init {
        _users.value = getUsersList(context)
        _clients.value = getClientsList(context)
    }

    open fun createUser(user: User) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${user.email}_id", user.id)
        editor.putString("${user.email}_name", user.name)
        editor.putString("${user.email}_idCard", user.idCard)
        editor.putString("${user.email}_role", user.role)
        editor.putString("${user.email}_phoneNumber", user.phoneNumber)
        editor.putString("${user.email}_password", user.password)
        editor.putString("${user.email}_address", user.address)
        editor.putBoolean("${user.email}_isActive", user.isActive)
        editor.putString("${user.email}_userAppConfigId", user.userAppConfigId)

        editor.putStringSet(
            "stored_emails",
            (sharedPreferences.getStringSet("stored_emails", emptySet()) ?: emptySet()).plus(user.email)
        )
        editor.apply()
    }

    open fun getUserById(userId: String): User? {
        val user = _users.value.find { it.id == userId}
        if (user == null){
            return _clients.value.find { it.id == userId}
        }
        return user
    }

    open fun getUserByEmail(email: String): User? {
        val user = _users.value.find { it.email == email}
        if (user == null){
            return _clients.value.find { it.email == email}
        }
        return user
    }

    open fun getUserByPhone(phoneNumber: String): User? {
        val user = _users.value.find { it.phoneNumber == phoneNumber}
        if (user == null){
            return _clients.value.find { it.phoneNumber == phoneNumber}
        }
        return user
    }
    open fun getUserByIdCard(idCard: String): User? {
        val user = _users.value.find { it.idCard == idCard}
        if (user == null){
            return _clients.value.find { it.idCard == idCard}
        }
        return user
    }
    open fun getUserByName(name: String): List<User> {
        return getUsersList(context).filter { it.name.contains(name, ignoreCase = true) }
    }

    open fun updateUser(user: User) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val existingUser = getUserById(user.id)
        if (existingUser != null) {
            editor.putString("${user.id}_id", user.id)
            editor.putString("${user.id}_name", user.name)
            editor.putString("${user.id}_idCard", user.idCard)
            editor.putString("${user.id}_role", user.role)
            editor.putString("${user.id}_phoneNumber", user.phoneNumber)
            editor.putString("${user.id}_password", user.password)
            editor.putString("${user.id}_address", user.address)
            editor.putBoolean("${user.id}_isActive", user.isActive)
            editor.putString("${user.id}_userAppConfigId", user.userAppConfigId)
            editor.putBoolean("${user.id}_isValidated", user.isValidated)
            editor.apply()

            _users.value = getUsersList(context)
        }
    }



    fun login(email: String, password: String): User? {

        val user = _users.value.find { it.email == email && it.password == password }
        if (user != null) {
            return user
        }


        val client = _clients.value.find { it.email == email && it.password == password }
        if (client != null) {

            return if (client.isActive) {
                client
            } else {

                null
            }
        }

        return null
    }


    open fun validateEmail(email: String): User? {
        val user = _users.value.find { it.email == email }
        if(user == null){
            return clients.value.find { it.email == email }
        }
        return user
    }


    open fun updatePassword(email: String, newPassword: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("${email}_password", newPassword)
        editor.apply()
    }

    protected open fun getUsersList(context: Context): List<User> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val storedUsers = mutableListOf<User>()
        val storedEmails = sharedPreferences.getStringSet("stored_emails", emptySet()) ?: emptySet()

        for (email in storedEmails) {
            val id = sharedPreferences.getString("${email}_id", "") ?: ""
            val name = sharedPreferences.getString("${email}_name", "") ?: ""
            val idCard = sharedPreferences.getString("${email}_idCard", "") ?: ""
            val role = sharedPreferences.getString("${email}_role", "") ?: "Client"
            val phoneNumber = sharedPreferences.getString("${email}_phoneNumber", "") ?: ""
            val password = sharedPreferences.getString("${email}_password", "") ?: ""
            val address = sharedPreferences.getString("${email}_address", "") ?: ""
            val isActive = sharedPreferences.getBoolean("${email}_isActive", true)
            val userAppConfigId = sharedPreferences.getString("${email}_userAppConfigId", "") ?: ""

            val user = User(
                id = id,
                name = name,
                idCard = idCard,
                role = role,
                email = email,
                phoneNumber = phoneNumber,
                password = password,
                address = address,
                isActive = isActive,
                userAppConfigId = userAppConfigId
            )
            storedUsers.add(user)
        }


        val adminEmail = "admin@unieventos.com"
        if (storedUsers.none { it.email == adminEmail }) {
            val hardcodedAdminUser = User(
                id = "admin1",
                name = "Admin User",
                idCard = "123456789",
                role = "Admin",
                email = adminEmail,
                phoneNumber = "1234567890",
                password = "admin123",
                address = "Admin Street 123",
                isActive = true,
                userAppConfigId = "configAdmin1"
            )
            storedUsers.add(hardcodedAdminUser)
        }

        return storedUsers
    }

    open  fun getClientsList(context: Context): List<Client> {
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
            val availableCoupons = sharedPreferences.getString("${clientId}_availableCoupons", "")?.split(",") ?: emptyList()
            val purchaseHistory = sharedPreferences.getString("${clientId}_purchaseHistory", "")?.split(",") ?: emptyList()
            val notifications = sharedPreferences.getString("${clientId}_notifications", "")?.split(",") ?: emptyList()
            val cartId = sharedPreferences.getString("${clientId}_cartId", "") ?: ""
            val friends = sharedPreferences.getString("${clientId}_friends", "")?.split(",") ?: emptyList()
            val isValidated = sharedPreferences.getBoolean("${clientId}_isValidated", false)

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
                friends = friends,
                isValidated = isValidated
            )
            storedClients.add(client)
        }

        return storedClients
    }
}


