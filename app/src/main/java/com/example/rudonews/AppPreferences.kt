package com.example.rudonews

import android.content.Context
import android.content.SharedPreferences
import com.example.rudonews.data.model.SharedPrefDepartments
import com.google.gson.Gson

class AppPreferences(val context: Context) {

    private fun getSharedPreferences(): SharedPreferences? {
        return context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    fun getText(key: String): String? {
        return getSharedPreferences()?.getString(key, "")
    }

    fun setText(key: String, value: String?) {
        this.getSharedPreferences()?.edit()?.putString(key, value)?.apply()
    }

    fun getBoolean(key: String): Boolean? {
        return getSharedPreferences()?.getBoolean(key, false)
    }

    fun setBoolean(key: String, value: Boolean) {
        this.getSharedPreferences()?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getObject(key: String): SharedPrefDepartments? {
        return Gson().fromJson(
            getSharedPreferences()?.getString(key, null),
            SharedPrefDepartments::class.java
        )
    }

    fun setObject(key: String, value: SharedPrefDepartments) {
        this.getSharedPreferences()?.edit()?.putString(key, Gson().toJson(value))?.apply()
    }

    fun clear() {
        this.getSharedPreferences()?.edit()?.clear()?.apply()
    }

    fun getAccessToken(): String? {
        return getSharedPreferences()?.getString(ACCESS_TOKEN, null)
    }

    fun setAccessToken(accessToken: String?) {
        this.getSharedPreferences()?.edit()?.putString(ACCESS_TOKEN, accessToken)?.apply()
    }

    fun removeAccessToken() {
        this.getSharedPreferences()?.edit()?.remove(ACCESS_TOKEN)?.apply()
    }

    fun getRefreshToken(): String? {
        return getSharedPreferences()?.getString(REFRESH_TOKEN, null)
    }

    fun setRefreshToken(refreshToken: String?) {
        this.getSharedPreferences()?.edit()?.putString(REFRESH_TOKEN, refreshToken)?.apply()
    }

    fun removeRefreshToken() {
        this.getSharedPreferences()?.edit()?.remove(REFRESH_TOKEN)?.apply()
    }

    companion object {
        const val PREF_FILE = "MyPreferences"
        const val DEPARTMENT_LIST = "department_list"
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }
}
