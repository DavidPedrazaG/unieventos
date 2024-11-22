package eam.edu.unieventos.model

class Client(
    var availableCoupons: List<String> = emptyList(),
    val purchaseHistory: List<String> = emptyList(),
    val friends: List<String> = emptyList(),
    val notifications: List<String> = emptyList(),
    var cartId: String = "",
    id: String = "",
    idCard: String = "",
    name: String = "",
    phoneNumber: String = "",
    address: String = "",
    email: String = "",
    password: String = "",
    isActive: Boolean = true,
    role: String = "",
    userAppConfigId: String = "",
    isValidated: Boolean = false
) : User(
    id = id,
    idCard = idCard,
    name = name,
    phoneNumber = phoneNumber,
    address = address,
    email = email,
    password = password,
    isActive = isActive,
    role = role,
    userAppConfigId = userAppConfigId,
    isValidated = isValidated
) {


    fun copyWithUpdatedFriends(newFriends: List<String>): Client {
        return Client(
            availableCoupons = this.availableCoupons,
            purchaseHistory = this.purchaseHistory,
            friends = newFriends,
            notifications = this.notifications,
            cartId = this.cartId,
            id = this.id,
            idCard = this.idCard,
            name = this.name,
            phoneNumber = this.phoneNumber,
            address = this.address,
            email = this.email,
            password = this.password,
            isActive = this.isActive,
            role = this.role,
            userAppConfigId = this.userAppConfigId,
            isValidated = this.isValidated
        )
    }
}
