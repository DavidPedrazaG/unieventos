package eam.edu.unieventos.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import eam.edu.unieventos.model.User

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")


    fun getUsers(): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()

        usersCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {

                return@addSnapshotListener
            }


            val usersList = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) }
            liveData.value = usersList ?: emptyList()
        }

        return liveData
    }


    fun addUser(user: User) {
        usersCollection.add(user)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }


    fun getUserById(userId: String): LiveData<User?> {
        val liveData = MutableLiveData<User?>()

        usersCollection.document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    liveData.value = user
                } else {
                    liveData.value = null
                }
            }
            .addOnFailureListener { e ->
                // Manejar el error
                liveData.value = null
            }

        return liveData
    }


    fun updateUser(userId: String, updatedUser: User) {
        usersCollection.document(userId).set(updatedUser)
            .addOnSuccessListener {
                // Usuario actualizado con éxito
            }
            .addOnFailureListener { e ->
                // Manejar el error
            }
    }


    fun deleteUser(userId: String) {
        usersCollection.document(userId).delete()
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }
}
