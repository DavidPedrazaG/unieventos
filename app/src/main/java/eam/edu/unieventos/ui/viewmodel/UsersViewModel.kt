package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.room.util.copy
import eam.edu.unieventos.model.AppUserConfiguration
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsersViewModel(private val context: Context) : ViewModel() {

    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        _users.value = getUsersList(context)
    }


    fun createUser(user: User) {
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
        editor.putString("${user.email}_userAppConfigId", user.userAppConfigId) // Almacenando el userAppConfigId


        editor.putStringSet(
            "stored_emails",
            (sharedPreferences.getStringSet("stored_emails", emptySet()) ?: emptySet()).plus(user.email)
        )
        editor.apply()
    }


    fun login(email: String, password: String): User? {
        return _users.value.find { it.email == email && it.password == password }
    }


    fun validateEmail(email: String): User? {
        return _users.value.find { it.email == email }
    }


    fun updatePassword(email: String, newPassword: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("${email}_password", newPassword)
        editor.apply()
    }


    private fun getUsersList(context: Context): List<User> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val storedUsers = mutableListOf<User>()
        val storedEmails = sharedPreferences.getStringSet("stored_emails", emptySet()) ?: emptySet()

        for (email in storedEmails) {
            val id = sharedPreferences.getString("${email}_id", "") ?: ""
            val name = sharedPreferences.getString("${email}_name", "") ?: ""
            val idCard = sharedPreferences.getString("${email}_idCard", "") ?: ""
            val role = sharedPreferences.getString("${email}_role", "") ?: "Client" // Por defecto es 'Client'
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


        return storedUsers + listOf(
            User(
                id = "123",
                name = "Juan",
                idCard = "12345",
                role = "Client",
                email = "juancitof@gmail.com",
                phoneNumber = "311311",
                password = "123456",
                address = "Calle Falsa 123",
                isActive = true,
                userAppConfigId = "config1"
            ),
            User(
                id = "1234",
                name = "Admin",
                idCard = "123",
                role = "Admin",
                email = "admin@gmail.com",
                phoneNumber = "311311",
                password = "123456",
                address = "Avenida Siempre Viva",
                isActive = true,
                userAppConfigId = "config2"
            )
        )
    }
}


