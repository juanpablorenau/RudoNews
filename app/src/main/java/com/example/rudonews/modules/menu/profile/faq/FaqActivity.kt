package com.example.rudonews.modules.menu.profile.faq

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rudonews.databinding.ActivityFaqBinding

class FaqActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqBinding
    private lateinit var viewModel: FaqViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[FaqViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initUi()
    }

    private fun initUi() {
        initToolbar()
        initData()
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initData() {
        viewModel.populateData()
    }
}
