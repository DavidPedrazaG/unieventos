package eam.edu.unieventos.model

 class Client(
    var availableCoupons: List<String>,  // List of coupon codes
    val purchaseHistory: List<String>,   // List of order IDs
    val friends: List<String>,           // List of client IDs (friend list)
    val notifications: List<String>,     // List of notification IDs
    val cartId: String?,
    id: String,
    idCard: String,
    name: String,
    phoneNumber: String,
    address: String,
    email: String,
    password: String,
    isActive: Boolean,
    role: String,
    userAppConfigId: String,
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

)
