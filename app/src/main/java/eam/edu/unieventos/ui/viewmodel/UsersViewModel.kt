package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.Role
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsersViewModel(private val context: Context) : ViewModel(){

    private val  _users = MutableStateFlow( emptyList<User>())

    val users: StateFlow< List<User>> = _users.asStateFlow()

    init {
        _users.value = getUsersList(context)
    }

    fun createUser(user: User, context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        editor.putString("${user.email}_id", user.id)
        editor.putString("${user.email}_name", user.name)
        editor.putString("${user.email}_cc", user.cc)
        editor.putString("${user.email}_role", user.role.toString())
        editor.putString("${user.email}_cellphone", user.cellphone)
        editor.putString("${user.email}_password", user.password)
        editor.putStringSet("stored_emails", (sharedPreferences.getStringSet("stored_emails", emptySet()) ?: emptySet()).plus(user.email))
        editor.apply()
    }


    fun login(email: String, password: String): User?{
        return _users.value.find { it.email == email && it.password == password}
    }

    fun validateEmail(email:String): User?{
        return _users.value.find { it.email == email}
    }

    fun updatePassword(context: Context, email: String, newPassword: String) {
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
            val cc = sharedPreferences.getString("${email}_cc", "") ?: ""
            val role = sharedPreferences.getString("${email}_role", Role.CLIENT.toString()) ?: Role.CLIENT.toString()
            val cellphone = sharedPreferences.getString("${email}_cellphone", "") ?: ""
            val password = sharedPreferences.getString("${email}_password", "") ?: ""

            val user = User(
                id = id,
                name = name,
                cc = cc,
                role = Role.valueOf(role),
                email = email,
                cellphone = cellphone,
                password = password
            )
            storedUsers.add(user)
        }


        return storedUsers + listOf(
            User(
                id = "123",
                name = "Juan",
                cc = "12345",
                role = Role.CLIENT,
                email = "juancitof@gmail.com",
                cellphone = "311311",
                password = "123456"
            ),
            User(
                id = "1234",
                name = "Admin",
                cc = "123",
                role = Role.ADMIN,
                email = "admin@gmail.com",
                cellphone = "311311",
                password = "123456"
            )
        )
    }

}

