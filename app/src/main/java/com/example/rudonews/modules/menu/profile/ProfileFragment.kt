package com.example.rudonews.modules.menu.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rudonews.App
import com.example.rudonews.R
import com.example.rudonews.databinding.FragmentProfileBinding
import com.example.rudonews.modules.login.LoginActivity
import com.example.rudonews.modules.menu.MenuActivity
import com.example.rudonews.modules.menu.profile.edit_profile.EditProfileActivity
import com.example.rudonews.modules.menu.profile.faq.FaqActivity
import com.example.rudonews.modules.menu.profile.privacy_policy.PrivacyPolicyActivity
import com.example.rudonews.modules.menu.profile.terms.TermsActivity
import com.example.rudonews.utils.Utils

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        binding = FragmentProfileBinding.bind(view)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewModel = viewModel

        App.user?.let {
            initObservers()
        } ?: context?.let { Utils.createGuestDialog(it) }

        return view
    }

    override fun onStart() {
        super.onStart()
        App.user?.let {
            viewModel.getLoggedUser()
        }
    }

    private fun initObservers() {
        viewModel.eventLoggedUserSuccessful.observe(viewLifecycleOwner) { eventLoggedUserSuccessful ->
            if (eventLoggedUserSuccessful) {
                viewModel.username.value = App.user?.fullname
                viewModel.email.value = App.user?.email
            } else {
                Utils.createDialog(App.instance, getString(R.string.server_error_dialog))
            }
        }

        viewModel.existsServerResponse.observe(viewLifecycleOwner) { existsResponse ->
            if (!existsResponse) {
                Utils.createDialog(App.instance, getString(R.string.no_server_response))
            }
        }

        viewModel.isNetworkAvailable.observe(viewLifecycleOwner) { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                Utils.createDialog(App.instance, getString(R.string.error_connection))
            }
        }
    }

    fun logOut() {
        App.preferences.removeAccessToken()
        App.user = null
        goToLoginActivity()
    }

    fun goToEditProfileActivity() {
        App.user?.let {
            startActivity(Intent(App.instance, EditProfileActivity::class.java))
        }
    }

    fun goToFaqActivity() {
        App.user?.let {
            startActivity(Intent(App.instance, FaqActivity::class.java))
        }
    }

    fun goToTermsActivity() {
        App.user?.let {
            startActivity(Intent(App.instance, TermsActivity::class.java))
        }
    }

    fun goToPrivacyPolicyActivity() {
        App.user?.let {
            startActivity(Intent(App.instance, PrivacyPolicyActivity::class.java))
        }
    }

    private fun goToLoginActivity() {
        App.user = null
        val menuActivity = activity as MenuActivity
        menuActivity.finish()
        App.preferences.removeAccessToken()
        App.preferences.removeRefreshToken()
        startActivity(Intent(App.instance, LoginActivity::class.java))
    }
}
