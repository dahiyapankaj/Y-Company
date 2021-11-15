package com.y.company.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.securepreferences.SecurePreferences
import com.y.company.R
import java.util.*


class SharedPrefManager {
    private var sharedPreferences: SharedPreferences? = null
    fun init(context: Context) {
        sharedPreferences = SecurePreferences(context, "", context.getString(R.string.app_name))
    }

    fun reset(context: Context) {
        sharedPreferences = null
        sharedPreferences = SecurePreferences(context, "", context.getString(R.string.app_name))
    }

    /**
     * Method to set string in preferences
     */
    private fun setStringInPreferences(key: String, value: String?) {
        val editor = sharedPreferences!!.edit()
        editor.putString(key, value ?: "")
        editor.apply()
    }

    /**
     * Method to get string from preferences
     */
    private fun getStringFromPreferences(key: String): String? {
        return sharedPreferences!!.getString(key, "")
    }

    /**
     * Method to set string set in preferences
     */
    private fun setStringSetInPreferences(
        key: String,
        value: Set<String>
    ): Boolean {
        val editor = sharedPreferences!!.edit()
        editor.putStringSet(key, value)
        return editor.commit()
    }

    /**
     * Method to get string set from preferences
     */
    private fun getStringSetFromPreferences(key: String): Set<String>? {
        return sharedPreferences!!.getStringSet(key, HashSet())
    }

    /**
     * Method to set int in preferences
     */
    private fun setIntInPreferences(key: String, value: Int): Boolean {
        val editor = sharedPreferences!!.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    /**
     * Method to get int from preferences
     */
    private fun getIntFromPreferences(key: String): Int {
        return sharedPreferences!!.getInt(key, -1)
    }

    /**
     * Method to set boolean in preferences
     */
    private fun setBooleanInPreferences(key: String, value: Boolean): Boolean {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    /**
     * Method to get boolean from preferences
     */
    private fun getBooleanFromPreferences(key: String): Boolean {
        return sharedPreferences!!.getBoolean(key, false)
    }


    /**
     * Removing all preferences.
     */
    fun clearAllPreferences() {
        sharedPreferences?.edit()?.clear()?.apply()
    }

    var accessToken: String?
        get() = getStringFromPreferences(ACCESS_TOKEN)
        set(token) {
            setStringInPreferences(ACCESS_TOKEN, token)
        }

    var refreshToken: String?
        get() = getStringFromPreferences(REFRESH_TOKEN)
        set(refreshToken) {
            setStringInPreferences(REFRESH_TOKEN, refreshToken)
        }


    var userId: String?
        get() = getStringFromPreferences(USER_ID)
        set(id) {
            setStringInPreferences(USER_ID, id)
        }


    var username: String?
        get() = getStringFromPreferences(USER_NAME)
        set(username) {
            setStringInPreferences(USER_NAME, username)
        }
    var userEmail: String?
        get() = getStringFromPreferences(USER_EMAIL)
        set(username) {
            setStringInPreferences(USER_EMAIL, username)
        }


    companion object {
        /**
         * Create a shared instance of the auth_tokenclass
         */
        var instance: SharedPrefManager? = null
            get() {
                if (field == null) {
                    field = SharedPrefManager()
                }
                return field
            }
            private set
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_ID = "user_id"
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
    }
}