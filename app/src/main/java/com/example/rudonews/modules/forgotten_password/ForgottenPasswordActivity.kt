package com.example.rudonews.modules.forgotten_password

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rudonews.R
import com.example.rudonews.databinding.ActivityForgottenPasswordBinding
import com.example.rudonews.utils.Utils

class ForgottenPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgottenPasswordBinding
    private lateinit var viewModel: ForgottenPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgottenPasswordBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ForgottenPasswordViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initToolbar()
        initObservers()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarForgottenPassword)
        binding.toolbarForgottenPassword.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.eventResetPasswordSuccessful.observe(this) { success ->
            val message = if (success) {
                getString(R.string.forgotten_password_success_dialog)
            } else {
                getString(R.string.forgotten_password_fail_dialog)
            }
            Utils.createDialog(this, message)
        }
    }
}
