package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eam.edu.unieventos.model.Client
import eam.edu.unieventos.model.Event
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

open class UsersViewModel(protected val context: Context) : ViewModel() {

    val db = Firebase.firestore
    private val dbcheck = FirebaseFirestore.getInstance()
    protected val _clients = MutableStateFlow(emptyList<Client>())
    protected val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    init {
        loadUsers()
    }


    fun loadUsers() {
        viewModelScope.launch {
            _users.value = getUserList()
            _clients.value = getClientsList()
        }
    }


    suspend fun checkIfClientExist(): Boolean {
        return try{
            val querySnapshot = dbcheck.collection("clients").get().await()
            !querySnapshot.isEmpty
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
    open fun createUser(user: User) {
        viewModelScope.launch {
            db.collection("users")
                .add(user)
                .await()
            loadUsers()
        }
    }

    open fun editUser(user: User) {
        viewModelScope.launch {
            db.collection("users")
                .document(user.id)
                .set(user)
                .await()

            _users.value = getUserList()
        }
    }


    suspend fun getUserById(userId: String): User? {

        val userSnapshot = db.collection("users")
            .document(userId)
            .get()
            .await()

        val user = userSnapshot.toObject(User::class.java)
        if (user != null) {
            user.id = userSnapshot.id
            return user
        }
        val clientSnapshot = db.collection("clients")
            .document(userId)
            .get()
            .await()

        val client = clientSnapshot.toObject(User::class.java)
        if (client != null) {
            client.id = clientSnapshot.id
            return client
        }

        return null
    }


    suspend fun getUserByEmail(email: String): User? {
        val userSnapshot = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        if (!userSnapshot.isEmpty) {
            val document = userSnapshot.documents.first()
            val user = document.toObject(User::class.java)
            user?.id = document.id
            return user
        }

        val clientSnapshot = db.collection("clients")
            .whereEqualTo("email", email)
            .get()
            .await()

        if (!clientSnapshot.isEmpty) {
            val document = clientSnapshot.documents.first()
            val client = document.toObject(Client::class.java)
            client?.id = document.id
            return client
        }

        return null
    }


    open suspend fun getUserByPhone(phoneNumber: String): User? {
        val userSnapshot = db.collection("users")
            .whereEqualTo("phoneNumber",phoneNumber)
            .get()
            .await()

        if (!userSnapshot.isEmpty) {
            val document = userSnapshot.documents.first()
            val user = document.toObject(User::class.java)
            user?.id = document.id
            return user
        }
        val clientSnapshot = db.collection("clients")
            .whereEqualTo("phoneNumber",phoneNumber)
            .get()
            .await()
        if (!clientSnapshot.isEmpty) {
            val document = clientSnapshot.documents.first()
            val client = document.toObject(Client::class.java)
            client?.id = document.id
            return client
        }
        return null
    }
    open suspend fun getUserByIdCard(idCard: String): User? {
        val userSnapshot = db.collection("users")
            .whereEqualTo("idCard",idCard)
            .get()
            .await()

        if (!userSnapshot.isEmpty) {
            val document = userSnapshot.documents.first()
            val user = document.toObject(User::class.java)
            user?.id = document.id
            return user
        }
        val clientSnapshot = db.collection("clients")
            .whereEqualTo("idCard",idCard)
            .get()
            .await()
        if (!clientSnapshot.isEmpty) {
            val document = clientSnapshot.documents.first()
            val client = document.toObject(Client::class.java)
            client?.id = document.id
            return client
        }
        return null
    }





    open suspend fun login(email: String, password: String): User? {

        if (email == "admin@gmail.com" && password == "1") {
            return User(
                id = "admin1",
                name = "Admin User",
                idCard = "123456789",
                role = "Admin",
                email = email,
                phoneNumber = "1234567890",
                password = password,
                address = "Admin Street 123",
                isActive = true,
                userAppConfigId = "configAdmin1",
                isValidated = true
            )
        }


        val userSnapshot = db.collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .await()



        if (userSnapshot.documents.isNotEmpty()) {
            val userDocument = userSnapshot.documents.first()
            val user = userDocument.toObject(User::class.java)
            user?.id = userDocument.id
            return user
        }


        val clientSnapshot = db.collection("clients")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .await()



        if (clientSnapshot.documents.isNotEmpty()) {
            val clientDocument = clientSnapshot.documents.first()
            val client = clientDocument.toObject(Client::class.java)
            client?.id = clientDocument.id
            client?.isValidated = clientDocument.getBoolean("isValidated") ?: false
            client?.isActive = clientDocument.getBoolean("isActive") ?: false
            if (client?.isActive == true) {
                return client
            }
        }


        return null
    }




    open fun validateEmail(email: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val userSnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!userSnapshot.isEmpty) {
                val document = userSnapshot.documents.first()
                val user = document.toObject(User::class.java)
                user?.id = document.id
                callback(user)
                return@launch
            }
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



    open fun updatePassword(email: String, newPassword: String, callback: (Boolean) -> Unit) {

        viewModelScope.launch {

            val userSnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!userSnapshot.isEmpty) {
                val userDoc = userSnapshot.documents.first()
                db.collection("users").document(userDoc.id)
                    .update("password", newPassword)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
                return@launch
            }
            val clientSnapshot = db.collection("clients")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!clientSnapshot.isEmpty) {
                val clientDoc = clientSnapshot.documents.first()
                db.collection("clients").document(clientDoc.id)
                    .update("password", newPassword)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
                return@launch
            }
            callback(false)
        }
    }






    open suspend fun getUserList(): List<User> {
        val snapshot = db.collection("users")
            .get()
            .await()

        val users = snapshot.documents.mapNotNull {
            val user = it.toObject(User::class.java)
            requireNotNull(user)
            user.id = it.id
            user
        }.toMutableList()


        val adminEmail = "admin@gmail.com"
        if (users.none { it.email == adminEmail }) {
            val hardcodedAdminUser = User(
                id = "admin1",
                name = "Admin User",
                idCard = "123456789",
                role = "Admin",
                email = adminEmail,
                phoneNumber = "1234567890",
                password = "1",
                address = "Admin Street 123",
                isActive = true,
                userAppConfigId = "configAdmin1",
                isValidated = true
            )
            users.add(hardcodedAdminUser)
        }

        return users
    }


    open suspend fun getClientsList(): List<Client> {

        val snapshot = db.collection("clients")
            .get()
            .await()


        return snapshot.documents.mapNotNull { document ->
            val client = document.toObject(Client::class.java)
            requireNotNull(client)
            client.id = document.id
            client
        }
    }


}


