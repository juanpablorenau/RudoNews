package com.example.rudonews.modules.change_password

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rudonews.R
import com.example.rudonews.databinding.ActivityChangePasswordBinding
import com.example.rudonews.utils.Utils

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: ChangePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initToolbar()
        initObservers()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarChangePassword)
        binding.toolbarChangePassword.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.areTheyEqual.observe(this) { areTheyEqual ->
            if (!areTheyEqual) {
                Utils.createDialog(this, getString(R.string.different_passwords))
            }
        }

        viewModel.hasCorrectFormat.observe(this) { hasCorrectFormat ->
            if (!hasCorrectFormat) {
                Utils.createDialog(this, getString(R.string.incorrect_format))
            }
        }

        viewModel.eventSavePasswordSuccessful.observe(this) { success ->
            val message = if (success) {
                getString(R.string.success_change_password)
            } else {
                getString(R.string.fail_change_password)
            }
            Utils.createDialog(this, message)
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
}
