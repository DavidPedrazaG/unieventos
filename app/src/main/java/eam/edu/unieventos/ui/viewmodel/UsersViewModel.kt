package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class UsersViewModel(protected val context: Context) : ViewModel() {

    protected val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        _users.value = getUsersList(context)
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
        return _users.value.find { it.id == userId }
    }

    open fun getUserByEmail(email: String): User? {
        return _users.value.find { it.email == email }
    }

    open fun getUserByPhone(phoneNumber: String): User? {
        return getUsersList(context).find { it.phoneNumber == phoneNumber }
    }
    open fun getUserByIdCard(idCard: String): User? {
        return getUsersList(context).find { it.idCard == idCard }
    }
    open fun getUserByName(name: String): List<User> {
        return getUsersList(context).filter { it.name.contains(name, ignoreCase = true) }
    }

    open fun updateUser(user: User) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val existingUser = getUserById(user.id)
        if (existingUser != null) {
            editor.putString("${user.email}_id", user.id)
            editor.putString("${user.email}_name", user.name)
            editor.putString("${user.email}_idCard", user.idCard)
            editor.putString("${user.email}_role", user.role)
            editor.putString("${user.email}_phoneNumber", user.phoneNumber)
            editor.putString("${user.email}_password", user.password)
            editor.putString("${user.email}_address", user.address)
            editor.putBoolean("${user.email}_isActive", user.isActive)
            editor.putString("${user.email}_userAppConfigId", user.userAppConfigId)
            editor.apply()

            _users.value = getUsersList(context)
        }
    }

    fun login(email: String, password: String): User? {
        return _users.value.find { it.email == email && it.password == password }
    }


    open fun validateEmail(email: String): User? {
        return _users.value.find { it.email == email }
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
}


