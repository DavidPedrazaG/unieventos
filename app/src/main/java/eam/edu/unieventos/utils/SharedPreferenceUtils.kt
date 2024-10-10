package eam.edu.unieventos.utils

import android.content.Context
import eam.edu.unieventos.dto.UserDTO
import eam.edu.unieventos.model.Role
import eam.edu.unieventos.model.User

object SharedPreferenceUtils {

    fun savePreference(context: Context, idUser: String, rol: String){
        val sharedPreferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("idUser", idUser)
        editor.putString("rol", rol)
        editor.apply()
    }

    fun clearPreference(context: Context){
        val sharedPreferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun getCurrenUser(context: Context): UserDTO?{
        val sharedPreferences = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val idUser = sharedPreferences.getString("idUser", "")
        val rol = sharedPreferences.getString("rol", "")

        return if(idUser.isNullOrEmpty() || rol.isNullOrEmpty()){
            return null
        }else{
            UserDTO(idUser, rol)
        }


    }
}