package com.example.rudonews.modules.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rudonews.R
import com.example.rudonews.databinding.ActivityLoginBinding
import com.example.rudonews.modules.forgotten_password.ForgottenPasswordActivity
import com.example.rudonews.modules.menu.MenuActivity
import com.example.rudonews.modules.register.RegisterActivity
import com.example.rudonews.utils.Utils

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initObservers()
    }

    private fun initObservers() {
        viewModel.email.observe(this) { email ->
            if (email.isNotEmpty() && viewModel.password.value?.isNotEmpty() == true) {
                binding.buttonLogin.isEnabled = true
                binding.buttonLogin.alpha = 1f
            } else {
                binding.buttonLogin.isEnabled = false
                binding.buttonLogin.alpha = SMALL_ALPHA
            }
        }

        viewModel.password.observe(this) { password ->
            if (password.isNotEmpty() && viewModel.email.value?.isNotEmpty() == true) {
                binding.buttonLogin.isEnabled = true
                binding.buttonLogin.alpha = 1f
            } else {
                binding.buttonLogin.isEnabled = false
                binding.buttonLogin.alpha = SMALL_ALPHA
            }
        }

        viewModel.eventLoginSuccessful.observe(this) { eventLoginSuccessful ->
            if (eventLoginSuccessful) {
                viewModel.getLoggedUser()
            } else {
                Utils.createDialog(this, getString(R.string.login_error_dialog))
            }
        }

        viewModel.eventLoggedUserSuccessful.observe(this) { eventLoggedUserSuccessful ->
            if (eventLoggedUserSuccessful) {
                goToMenuActivity()
            } else {
                Utils.createDialog(this, getString(R.string.server_error_dialog))
            }
        }

        viewModel.existsServerResponse.observe(this) { existsResponse ->
            if (!existsResponse) {
                Utils.createDialog(this, getString(R.string.no_server_response))
            }
        }

        viewModel.isNetworkAvailable.observe(this) { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                Utils.createDialog(this, getString(R.string.error_connection))
            }
        }
    }

    fun goToRegisterActivity() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun goToForgottenPasswordActivity() {
        startActivity(Intent(this, ForgottenPasswordActivity::class.java))
    }

    fun goToMenuActivity() {
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }

    companion object {
        const val SMALL_ALPHA = 0.2F
    }
}
