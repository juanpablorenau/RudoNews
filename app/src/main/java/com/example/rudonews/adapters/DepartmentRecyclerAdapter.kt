package com.example.rudonews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.rudonews.R
import com.example.rudonews.data.model.Department
import com.example.rudonews.databinding.ItemDepartmentBinding

class DepartmentRecyclerAdapter() : RecyclerView.Adapter<DepartmentRecyclerAdapter.ViewHolder>() {

    private lateinit var onDepartmentClickListener: DepartmentRecyclerViewInterface
    private lateinit var onCheckClickListener: DepartmentRecyclerViewInterface

    constructor(
        onDepartmentClickListener: DepartmentRecyclerViewInterface,
        onCheckClickListener: DepartmentRecyclerViewInterface
    ) : this() {
        this.onDepartmentClickListener = onDepartmentClickListener
        this.onCheckClickListener = onCheckClickListener
    }

    var data = listOf<Department>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])

        if (this::onDepartmentClickListener.isInitialized) {
            holder.itemView.setOnClickListener {
                onDepartmentClickListener.onDepartmentClickListener(data[position], position)
            }
        }
        if (this::onCheckClickListener.isInitialized) {
            val checkBox = holder.itemView.findViewById<CheckBox>(R.id.checkbox_department)
            checkBox.setOnClickListener {
                onCheckClickListener.onCheckClickListener(data[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface DepartmentRecyclerViewInterface {
        fun onDepartmentClickListener(department: Department, position: Int)
        fun onCheckClickListener(department: Department, position: Int)
    }

    class ViewHolder private constructor(private val binding: ItemDepartmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Department
        ) {
            binding.department = item

            if (item.isChecked) {
                binding.checkboxDepartment.isChecked = true
                binding.itemConstraintLayout.setBackgroundColor(itemView.context.getColor(R.color.light_red))
            } else {
                binding.checkboxDepartment.isChecked = false
                binding.itemConstraintLayout.setBackgroundColor(itemView.context.getColor(R.color.white))
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemDepartmentBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
