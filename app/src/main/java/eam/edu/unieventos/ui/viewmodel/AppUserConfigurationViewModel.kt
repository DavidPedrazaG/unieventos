package eam.edu.unieventos.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import eam.edu.unieventos.model.AppUserConfiguration
import eam.edu.unieventos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppUserConfigurationViewModel(private val context: Context) : ViewModel() {

    private val _appUserConfigurations = MutableStateFlow(emptyList<AppUserConfiguration>())
    val appUserConfigurations: StateFlow<List<AppUserConfiguration>> = _appUserConfigurations.asStateFlow()

    init {
        _appUserConfigurations.value = getAppUserConfigurationsList(context)
    }

    fun createAppUserConfig(userId: String, appUserConfig: AppUserConfiguration) {
        val sharedPreferences = context.getSharedPreferences("AppUserConfigPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${userId}_data1", appUserConfig.data1)
        editor.putString("${userId}_data2", appUserConfig.data2)
        editor.putString("${userId}_data3", appUserConfig.data3)

        editor.putStringSet(
            "stored_appUserConfigs",
            (sharedPreferences.getStringSet("stored_appUserConfigs", emptySet()) ?: emptySet()).plus(userId)
        )
        editor.apply()
    }

    fun getAppUserConfigurationsList(context: Context): List<AppUserConfiguration> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppUserConfigPrefs", Context.MODE_PRIVATE)
        val storedAppUserConfigs = mutableListOf<AppUserConfiguration>()
        val storedUserIds = sharedPreferences.getStringSet("stored_appUserConfigs", emptySet()) ?: emptySet()

        for (userId in storedUserIds) {
            val data1 = sharedPreferences.getString("${userId}_data1", "") ?: ""
            val data2 = sharedPreferences.getString("${userId}_data2", "") ?: ""
            val data3 = sharedPreferences.getString("${userId}_data3", "") ?: ""

            val appUserConfig = AppUserConfiguration(
                userId = userId,
                data1 = data1,
                data2 = data2,
                data3 = data3
            )

            storedAppUserConfigs.add(appUserConfig)
        }

        return storedAppUserConfigs
    }



    fun updateAppUserConfig(userId: String, appUserConfig: AppUserConfiguration) {
        val sharedPreferences = context.getSharedPreferences("AppUserConfigPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("${userId}_data1", appUserConfig.data1)
        editor.putString("${userId}_data2", appUserConfig.data2)
        editor.putString("${userId}_data3", appUserConfig.data3)
        editor.apply()

        _appUserConfigurations.value = getAppUserConfigurationsList(context)
    }

    fun getAppUserConfigurationByUser(userId: String): AppUserConfiguration? {
        return _appUserConfigurations.value.find { it.userId == userId }
    }
}
