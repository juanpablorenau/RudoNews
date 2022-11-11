package com.example.rudonews.modules.register

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.rudonews.App
import com.example.rudonews.AppPreferences
import com.example.rudonews.R
import com.example.rudonews.data.model.calls.RegisterRequest
import com.example.rudonews.databinding.ActivityRegisterBinding
import com.example.rudonews.modules.menu.MenuActivity
import com.example.rudonews.utils.Utils

class RegisterActivity : AppCompatActivity() {

    private val preferences = App.preferences

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initUi()
        initObservers()
    }

    override fun onResume() {
        initDepartments()
        super.onResume()
    }

    private fun initUi() {
        initToolbar()
        initRadioButtonText()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            goToLoginActivity()
        }
    }

    private fun initDepartments() {
        val sharedPrefDepartment =
            preferences.getObject(AppPreferences.DEPARTMENT_LIST)
        sharedPrefDepartment?.let {
            viewModel.departmentList.value =
                sharedPrefDepartment.deparments.filter { department -> department.isChecked }
                    .toMutableList()
            val text =
                viewModel.departmentList.value?.joinToString(", ") { department -> department.name.toString() }
            if (text.isNullOrEmpty()) {
                binding.textDepartments.text = getString(R.string.departments)
                enableViews(false)
            } else {
                binding.textDepartments.text = text
                enableViews(true)
            }
        } ?: run { binding.textDepartments.text = getString(R.string.departments) }
    }

    private fun initRadioButtonText() {
        val confirmTerms1 = getString(R.string.confirm_terms_part_1)
        val confirmTerms2 = getString(R.string.confirm_terms_part_2)
        val spannableText = getString(R.string.confirm_terms, confirmTerms1, confirmTerms2)

        val spannable = SpannableString(spannableText)
        val spannablePos = spannable.indexOf(confirmTerms2)

        spannable.setSpan(
            UnderlineSpan(),
            spannablePos,
            spannablePos + confirmTerms2.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)),
            spannablePos,
            spannablePos + confirmTerms2.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.radioButton.movementMethod = LinkMovementMethod.getInstance()
        binding.radioButton.text = spannable
    }

    private fun initObservers() {
        viewModel.username.observe(this) {
            if (viewModel.usernameError.value != 0) {
                if (viewModel.checkUsername()) {
                    binding.textLayoutUsername.error = null
                }
            }
        }

        viewModel.usernameError.observe(this) { error ->
            if (error != 0) {
                binding.editUsername.background = ContextCompat.getDrawable(this, R.color.light_red)
                binding.textLayoutUsername.error = getString(error)
            } else {
                binding.editUsername.background = ContextCompat.getDrawable(this, R.color.white)
            }
        }

        viewModel.email.observe(this) {
            if (viewModel.emailError.value != 0) {
                if (viewModel.checkEmail()) {
                    binding.textLayoutEmail.error = null
                }
            }
        }

        viewModel.emailError.observe(this) { error ->
            if (error != 0) {
                binding.editEmail.background = ContextCompat.getDrawable(this, R.color.light_red)
                binding.textLayoutEmail.error = getString(error)
            } else {
                binding.editEmail.background = ContextCompat.getDrawable(this, R.color.white)
            }
        }

        viewModel.password.observe(this) { _ ->
            viewModel.checkPassword()
        }

        viewModel.hasCapitalLetter.observe(this) { hasCapitalLetter ->
            setTextViewAppearance(binding.textCapitalLetters, hasCapitalLetter)
        }

        viewModel.hasLowercase.observe(this) { hasLowercase ->
            setTextViewAppearance(binding.textLowercase, hasLowercase)
        }

        viewModel.hasMinSize.observe(this) { hasMinSize ->
            setTextViewAppearance(binding.textEightCharacters, hasMinSize)
        }

        viewModel.hasNumber.observe(this) { hasNumber ->
            setTextViewAppearance(binding.textNumbers, hasNumber)
        }

        viewModel.hasSpecialCharacter.observe(this) { hasSpecialCharacter ->
            setTextViewAppearance(binding.textSpecialCharacter, hasSpecialCharacter)
        }

        viewModel.areAllParametersOk.observe(this) { areAllParametersOk ->
            if (areAllParametersOk) {
                if (binding.radioButton.isChecked) {
                    val departmentIds =
                        viewModel.departmentList.value?.joinToString(" ") { it.id.toString() }
                    val register = RegisterRequest(
                        viewModel.username.value,
                        viewModel.email.value,
                        viewModel.password.value,
                        departmentIds
                    )
                    viewModel.register(register)
                } else {
                    Utils.createDialog(this, getString(R.string.click_on_radio_button))
                }
            }
        }

        viewModel.eventRegisterSuccessful.observe(this) { eventRegisterSuccessful ->
            if (eventRegisterSuccessful) {
                viewModel.getLoggedUser()
            } else {
                Utils.createDialog(this, getString(R.string.server_error_dialog))
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

    private fun setTextViewAppearance(textView: TextView, available: Boolean) {
        if (available) {
            textView.background =
                ContextCompat.getDrawable(this, R.drawable.correct_rounded_text_view)
            textView.setTextColor(getColor(R.color.green))
        } else {
            textView.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_text_view)
            textView.setTextColor(getColor(R.color.gray))
        }
    }

    private fun enableViews(enable: Boolean) {
        binding.textLayoutUsername.isEnabled = enable
        binding.textLayoutEmail.isEnabled = enable
        binding.textLayoutPassword.isEnabled = enable
        binding.radioButton.isEnabled = enable
        binding.continueButton.isEnabled = enable
    }

    fun goToDepartments() {
        startActivity(Intent(this, DepartmentsActivity::class.java))
    }

    private fun goToLoginActivity() {
        onBackPressed()
    }

    private fun goToMenuActivity() {
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }
}
