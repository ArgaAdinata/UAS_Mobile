package com.example.uas_mobile_argaadinata_514333

import android.content.Context

class PrefManager private constructor(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_FILENAME = "AuthAppPref"
        private const val KEY_USERNAME = "Username"
        private const val KEY_NAME = "Name"
        private const val KEY_ID = "Id"
        private const val KEY_ADDRESS = "Address"
        private const val KEY_PHONE = "Phone"

        @Volatile
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: synchronized(this) {
                instance ?: PrefManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply()
    }
    fun saveName(name: String) {
        sharedPreferences.edit().putString(KEY_NAME, name).apply()
    }

    fun saveId(id: String) {
        sharedPreferences.edit().putString(KEY_ID, id).apply()
    }

    fun saveAddress(address: String) {
        sharedPreferences.edit().putString(KEY_ADDRESS, address).apply()
    }

    fun savePhone(phone: String) {
        sharedPreferences.edit().putString(KEY_PHONE, phone).apply()
    }

    fun getUsername(): String = sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    fun getName(): String = sharedPreferences.getString(KEY_NAME, "") ?: ""
    fun getId(): String = sharedPreferences.getString(KEY_ID, "") ?: ""
    fun getAddress(): String = sharedPreferences.getString(KEY_ADDRESS, "") ?: ""
    fun getPhone(): String = sharedPreferences.getString(KEY_PHONE, "") ?: ""

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}