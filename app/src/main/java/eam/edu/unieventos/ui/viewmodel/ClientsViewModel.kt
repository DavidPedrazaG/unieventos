package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Cart
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.Coupon
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.Notification
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import eam.edu.unieventos.ui.viewmodel.CartViewModel
import eam.edu.unieventos.ui.viewmodel.CouponsViewModel
import eam.edu.unieventos.ui.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class ClientsViewModel(context: Context) : UsersViewModel(context) {



    private val _couponViewModel = CouponsViewModel()
    private val _notificationViewModel = NotificationViewModel(context)
    private val _cartViewModel = CartViewModel(context)



    override fun createUser(user: User) {
        if (user is Client) {
            val clientData = hashMapOf(
                "id" to user.id,
                "name" to user.name,
                "idCard" to user.idCard,
                "role" to "Client",
                "email" to user.email,
                "address" to user.address,
                "phoneNumber" to user.phoneNumber,
                "password" to user.password,
                "isActive" to user.isActive,
                "userAppConfigId" to user.userAppConfigId,
                "isValidated" to user.isValidated,
                "friends" to user.friends,
                "availableCoupons" to user.availableCoupons,
                "purchaseHistory" to user.purchaseHistory,
                "notifications" to user.notifications
            )

            db.collection("clients").document(user.id).set(clientData)
                .addOnSuccessListener {

                    val halfIdCardLength = user.idCard.length / 2
                    val halfIdCard = user.idCard.substring(0, halfIdCardLength)
                    val randomCode = "${user.id}$halfIdCard"
                    val cart = Cart(id = randomCode, user.idCard)
                    _cartViewModel.addCart(cart, context)


                    val couponCode = _couponViewModel.generateCouponCode()
                    val expirationDate = Calendar.getInstance().apply {
                        add(Calendar.YEAR, 10)
                    }.time
                    val coupon = Coupon(
                        id = "", code = couponCode, discountPercentage = 15f,
                        expirationDate = expirationDate, eventCode = null, isActive = true
                    )
                    _couponViewModel.createCoupon(coupon)


                    val notificationId = _notificationViewModel.generateNotificationId()
                    val notification = Notification(
                        id = notificationId, from = "UniEventos", to = user.id,
                        message = "Recibiste un cupón del 15% de descuento", eventId = "",
                        sendDate = Calendar.getInstance().time, isRead = false
                    )
                    _notificationViewModel.createNotification(notification)
                }
                .addOnFailureListener {

                }
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
        val userRef = db.collection("clients").document(userId)


        userRef.update("friends", updatedFriends)
            .addOnSuccessListener {

                viewModelScope.launch {
                    _users.value = getUserList()
                }
            }
    }

    override suspend fun getUserList(): List<User> {
        return getClientsList()
    }

    suspend fun getByUserId(userId: String): User? {
        val clientSnapshot = db.collection("clients")
            .document(userId)
            .get()
            .await()

        val client = clientSnapshot.toObject(Client::class.java)
        if (client != null) {
            client.id = clientSnapshot.id
            return client
        }

        return null
    }

    fun updateClient(user: Client) {
        val clientData = hashMapOf(
            "id" to user.id,
            "name" to user.name,
            "idCard" to user.idCard,
            "role" to user.role,
            "phoneNumber" to user.phoneNumber,
            "password" to user.password,
            "address" to user.address,
            "isActive" to user.isActive,
            "userAppConfigId" to user.userAppConfigId,
            "isValidated" to user.isValidated,
            "friends" to user.friends,
            "availableCoupons" to user.availableCoupons,
            "purchaseHistory" to user.purchaseHistory,
            "notifications" to user.notifications
        )

        db.collection("clients").document(user.id).set(clientData)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _users.value = getUserList()
                }

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



    suspend fun getByPhone(phone: String): User? {
        val clientSnapshot = db.collection("clients")
            .whereEqualTo("phoneNumber",phone)
            .get()
            .await()

        return if (!clientSnapshot.isEmpty) {
            val document = clientSnapshot.documents.first()
            val user = document.toObject(User::class.java)
            user?.id = document.id
            user
        } else {
            null
        }
    }
    suspend fun getByIdCard(idCard: String): User? {
        val clientSnapshot = db.collection("clients")
            .whereEqualTo("idCard",idCard)
            .get()
            .await()

        return if (!clientSnapshot.isEmpty) {
            val document = clientSnapshot.documents.first()
            val user = document.toObject(User::class.java)
            user?.id = document.id
            user
        } else {
            null
        }
    }

    override fun validateEmail(email: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val clientSnapshot = db.collection("clients")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!clientSnapshot.isEmpty) {
                val document = clientSnapshot.documents.first()
                val client = document.toObject(Client::class.java)
                client?.id = document.id
                callback(client)
                return@launch
            }


            callback(null)
        }
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

