package com.example.rudonews.modules.menu.profile.edit_profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rudonews.App
import com.example.rudonews.App.Companion.preferences
import com.example.rudonews.AppPreferences
import com.example.rudonews.R
import com.example.rudonews.databinding.ActivityEditProfileBinding
import com.example.rudonews.modules.change_password.ChangePasswordActivity
import com.example.rudonews.modules.register.DepartmentsActivity
import com.example.rudonews.utils.Utils

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel

    private val departmentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                initDepartmentsFromSharedPref()
            } else {
                viewModel.getLoggedUser()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initUi()
        initObservers()
    }

    private fun initDepartmentsFromSharedPref() {
        val sharedPrefDepartment =
            preferences.getObject(AppPreferences.DEPARTMENT_LIST)
        sharedPrefDepartment?.let {
            viewModel.departmentList.value =
                sharedPrefDepartment.deparments.filter { department -> department.isChecked }
                    .toMutableList()
            val text =
                viewModel.departmentList.value?.joinToString(", ") { department -> department.name.toString() }
            viewModel.departmentsText.value = text
        }
    }

    private fun initUi() {
        initToolbar()
        viewModel.getLoggedUser()
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            goToProfileFragment()
        }
    }

    private fun initObservers() {
        viewModel.eventLoggedUserSuccessful.observe(this) { eventLoggedUserSuccessful ->
            if (eventLoggedUserSuccessful) {
                viewModel.username.value = App.user?.fullname
                viewModel.email.value = App.user?.email
                val text =
                    App.user?.departments?.joinToString(", ") { department -> department.name.toString() }
                viewModel.departmentsText.value = text
            } else {
                Utils.createDialog(this, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventSaveChangeSuccessful.observe(this) { eventSaveChangeSuccessful ->
            if (eventSaveChangeSuccessful) {
                Utils.createDialog(this, getString(R.string.save_changes_dialog))
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

    fun goToDepartmentsActivity() {
        val intent = Intent(this, DepartmentsActivity::class.java)
        departmentLauncher.launch(intent)
    }

    fun goToChangePasswordActivity() {
        startActivity(Intent(this, ChangePasswordActivity::class.java))
    }

    private fun goToProfileFragment() {
        onBackPressed()
    }
}
