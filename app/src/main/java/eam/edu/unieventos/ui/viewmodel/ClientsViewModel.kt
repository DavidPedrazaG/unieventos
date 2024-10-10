package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.model.Notification
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import eam.edu.unieventos.ui.viewmodel.CartViewModel
import eam.edu.unieventos.ui.viewmodel.CouponsViewModel
import eam.edu.unieventos.ui.viewmodel.NotificationViewModel
import java.util.Calendar

class ClientsViewModel(context: Context) : UsersViewModel(context) {

    private val _couponViewModel = CouponsViewModel(context)
    private val _notificationViewModel = NotificationViewModel(context)
    private val _cartViewModel = CartViewModel(context)



    override fun createUser(user: User) {
        if (user is Client) {
            val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("${user.id}_id", user.id)
            editor.putString("${user.id}_name", user.name)
            editor.putString("${user.id}_idCard", user.idCard)
            editor.putString("${user.id}_role", "Client")
            editor.putString("${user.id}_email", user.email)
            editor.putString("${user.id}_address", user.address)
            editor.putString("${user.id}_phoneNumber", user.phoneNumber)
            editor.putString("${user.id}_password", user.password)
            editor.putBoolean("${user.id}_isActive", user.isActive)
            editor.putString("${user.id}_userAppConfigId", user.userAppConfigId)
            editor.putBoolean("${user.id}_isValidated", user.isValidated)
            editor.putString("${user.id}_friends", user.friends.joinToString(","))
            editor.putString("${user.id}_availableCoupons", user.availableCoupons.joinToString(","))
            editor.putString("${user.id}_purchaseHistory", user.purchaseHistory.joinToString(","))
            editor.putString("${user.id}_notifications", user.notifications.joinToString(","))

            val halfIdCardLength = user.idCard.length / 2
            val halfIdCard = user.idCard.substring(0, halfIdCardLength)
            val randomCode = "${user.id}$halfIdCard"

            val cart = Cart(id = randomCode, user.idCard)
            editor.putString("${user.id}_cartId", cart.id)
            _cartViewModel.addCart(cart, context)

            val couponId = _couponViewModel.generateCouponId()
            val couponCode = _couponViewModel.generateCouponCode()
            val expirationDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, 10)
            }.time

            val coupon = Coupon(id = couponId, code = couponCode, discountPercentage = 15f, expirationDate = expirationDate, eventCode = null,1 , isActive = true)
            _couponViewModel.createCoupon(coupon)


            val notificationId = _notificationViewModel.generateNotificationId()
            val notification = Notification(id = notificationId, from = "UniEventos", to = user.id, message = "Recibiste un cupon del 15% de descuento", eventId = "", sendDate = Calendar.getInstance().time, isRead = false)
            _notificationViewModel.createNotification(notification)
            editor.putStringSet(
                "stored_client_ids",
                (sharedPreferences.getStringSet("stored_client_ids", emptySet()) ?: emptySet()).plus(user.id)
            )
            editor.apply()
        } else {
            super.createUser(user)
        }
    }


    fun addFriend(userId: String, friendId: String) {
        val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
        val currentFriends = sharedPreferences.getString("${userId}_friends", "")?.split(",")?.toMutableList() ?: mutableListOf()

        if (!currentFriends.contains(friendId)) {
            currentFriends.add(friendId)
            updateFriends(userId, currentFriends)
        }
    }


    fun removeFriend(userId: String, friendId: String) {
        val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
        val currentFriends = sharedPreferences.getString("${userId}_friends", "")?.split(",")?.toMutableList() ?: mutableListOf()

        if (currentFriends.contains(friendId)) {
            currentFriends.remove(friendId)
            updateFriends(userId, currentFriends)
        }
    }


    fun listFriends(userId: String): List<String> {
        val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("${userId}_friends", "")?.split(",") ?: emptyList()
    }


    private fun updateFriends(userId: String, updatedFriends: List<String>) {
        val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("${userId}_friends", updatedFriends.joinToString(","))
        editor.apply()


        _clients.value = getClientsList(context)
    }

    override fun getUsersList(context: Context): List<User> {
        return getClientsList(context)
    }

    fun getByUserId(userId: String): User? {
        return _clients.value.find { it.id == userId }
    }

    fun updateClient(user: Client) {
        val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
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
            editor.putString("${user.id}_friends", user.friends.joinToString(","))
            editor.putString("${user.id}_availableCoupons", user.availableCoupons.joinToString(","))
            editor.putString("${user.id}_purchaseHistory", user.purchaseHistory.joinToString(","))
            editor.putString("${user.id}_notifications", user.notifications.joinToString(","))
            editor.apply()

            _users.value = getUsersList(context)
        }
    }

    fun deactivateClient(client: Client) {
        if (client.isActive) {
            client.isActive = false

            updateClient(client)
        }
    }
    open fun getDeactivatedClientByEmail(email: String): Client? {
        return _clients.value.find { it.email == email && !it.isActive }
    }


    fun getByPhone(phone: String): User? {
        return _clients.value.find { it.phoneNumber == phone }
    }
    fun getByIdCard(idCard: String): User? {
        return _clients.value.find { it.idCard == idCard }
    }

    override fun validateEmail(email: String): User? {
        return _clients.value.find { it.email == email }
    }

    fun updatePasswordClient(client:Client, newPassword: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("${client.id}_password", newPassword)
        editor.apply()
    }

    fun generateUserId(): String {
        val uniqueId = System.currentTimeMillis().toString() + (0..999).random().toString()
        return uniqueId
    }




}

