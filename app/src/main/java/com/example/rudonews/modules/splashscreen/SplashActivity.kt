package com.example.rudonews.modules.splashscreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rudonews.App
import com.example.rudonews.R
import com.example.rudonews.modules.login.LoginActivity
import com.example.rudonews.modules.menu.MenuActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
            setTheme(R.style.SplashTheme)
        } else {
            setTheme(R.style.SplashScreenAPI12)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        App.user?.let {
            startActivity(Intent(this, MenuActivity::class.java))
        } ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
