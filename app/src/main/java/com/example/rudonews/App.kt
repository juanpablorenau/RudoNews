package com.example.rudonews

import android.app.Application
import com.example.rudonews.data.model.User

class App : Application() {

    companion object {
        lateinit var instance: App private set
        lateinit var preferences: AppPreferences
        var user: User? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = AppPreferences(instance)
    }
}

