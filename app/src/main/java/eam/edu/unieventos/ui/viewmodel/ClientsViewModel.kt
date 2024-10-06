
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
import eam.edu.unieventos.ui.viewmodel.CouponViewModel
import eam.edu.unieventos.ui.viewmodel.NotificationViewModel
import java.util.Calendar

class ClientViewModel(context: Context) : UsersViewModel(context) {

    private val _couponViewModel = CouponViewModel(context)
    private val _notificationViewModel = NotificationViewModel(context)
    private val _cartViewModel = CartViewModel(context)
    private val _clients = MutableStateFlow(emptyList<Client>())
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    init {
        _clients.value = getClientsList(context)
    }


    override fun createUser(user: User) {
        if (user is Client) {
            val sharedPreferences = context.getSharedPreferences("ClientPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("${user.id}_id", user.id)
            editor.putString("${user.id}_name", user.name)
            editor.putString("${user.id}_idCard", user.idCard)
            editor.putString("${user.id}_role", user.role)
            editor.putString("${user.id}_email", user.email)
            editor.putString("${user.id}_address", user.address)
            editor.putString("${user.id}_phoneNumber", user.phoneNumber)
            editor.putString("${user.id}_password", user.password)
            editor.putBoolean("${user.id}_isActive", user.isActive)
            editor.putString("${user.id}_userAppConfigId", user.userAppConfigId)
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

            val coupon = Coupon(id = couponId, code = couponCode, discountPercentage = 15f, expirationDate = expirationDate, eventId = null, isActive = true)
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

    fun getByEmail(email: String): User? {
        return _clients.value.find { it.email == email }
    }

    fun getByPhone(phone: String): User? {
        return _clients.value.find { it.phoneNumber == phone }
    }
    fun getByIdCard(idCard: String): User? {
        return _clients.value.find { it.idCard == idCard }
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
            val availableCoupons = sharedPreferences.getString("${clientId}_availableCoupons", "")?.split(",") ?: emptyList()
            val purchaseHistory = sharedPreferences.getString("${clientId}_purchaseHistory", "")?.split(",") ?: emptyList()
            val notifications = sharedPreferences.getString("${clientId}_notifications", "")?.split(",") ?: emptyList()
            val cartId = sharedPreferences.getString("${clientId}_cartId", "") ?: ""
            val friends = sharedPreferences.getString("${clientId}_friends", "")?.split(",") ?: emptyList()

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

        return storedClients
    }
}

