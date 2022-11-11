package com.example.rudonews.modules.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rudonews.R
import com.example.rudonews.adapters.DepartmentRecyclerAdapter
import com.example.rudonews.data.model.Department
import com.example.rudonews.databinding.ActivityDepartmentsBinding
import com.example.rudonews.utils.Utils

class DepartmentsActivity :
    AppCompatActivity(),
    DepartmentRecyclerAdapter.DepartmentRecyclerViewInterface {

    private lateinit var binding: ActivityDepartmentsBinding
    private lateinit var viewModel: DepartmentsViewModel

    private lateinit var departmentRecyclerAdapter: DepartmentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepartmentsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DepartmentsViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initUi()
        initObservers()
    }

    override fun onDepartmentClickListener(department: Department, position: Int) {
        department.isChecked = !department.isChecked
        departmentRecyclerAdapter.notifyItemChanged(position)
    }

    override fun onCheckClickListener(department: Department, position: Int) {
        department.isChecked = !department.isChecked
        departmentRecyclerAdapter.notifyItemChanged(position)
    }

    private fun initUi() {
        initToolbar()
        initData()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initData() {
        viewModel.getList()
        if (viewModel.departmentList.value?.isNotEmpty() == true) {
            initAdapter()
        }
    }

    private fun initObservers() {
        viewModel.eventDepartmentsSuccessful.observe(this) { eventDepartmentsSuccessful ->
            if (eventDepartmentsSuccessful) {
                viewModel.pager.value?.next?.let {
                    viewModel.pager.value?.page?.plus(1)
                        ?.let { page -> viewModel.getDepartments(page) }
                } ?: run {
                    binding.progressTopBar.isVisible = false
                    initAdapter()
                }
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

    private fun initAdapter() {
        departmentRecyclerAdapter = DepartmentRecyclerAdapter(this, this)
        viewModel.departmentList.value?.sortBy { it.name }
        departmentRecyclerAdapter.data = viewModel.departmentList.value ?: listOf()
        binding.recyclerView.adapter = departmentRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun accept() {
        val departmentNames = arrayListOf<CharSequence>()
        viewModel.departmentList.value?.forEach {
            if (it.isChecked) {
                it.name?.let { name -> departmentNames.add(name) }
            }
        }
        if (departmentNames.isNotEmpty()) {
            viewModel.saveList()
            setResult(RESULT_OK)
            onBackPressed()
        } else {
            Utils.createDialog(this, getString(R.string.departments_dialog))
        }
    }
}
