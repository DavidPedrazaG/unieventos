package eam.edu.unieventos.model

class Client(
    var availableCoupons: List<String> = emptyList(),
    val purchaseHistory: List<String> = emptyList(),
    val friends: List<String> = emptyList(),
    val notifications: List<String> = emptyList(),
    val cartId: String? = null,
    id: String = "",
    idCard: String = "",
    name: String = "",
    phoneNumber: String = "",
    address: String = "",
    email: String = "",
    password: String = "",
    isActive: Boolean,
    role: String = "",
    userAppConfigId: String = "",
    isValidated: Boolean
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
    constructor() : this(
        availableCoupons = emptyList(),
        purchaseHistory = emptyList(),
        friends = emptyList(),
        notifications = emptyList(),
        cartId = null,
        isActive = true,
        isValidated = false
    )
}
