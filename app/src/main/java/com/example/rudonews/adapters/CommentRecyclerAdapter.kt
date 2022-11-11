package com.example.rudonews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rudonews.data.model.Comment
import com.example.rudonews.databinding.ItemCommentBinding

class CommentRecyclerAdapter() : RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {

    var data = listOf<Comment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder private constructor(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Comment
        ) {
            binding.comment = item
            item.user?.departments?.let {
                binding.textDepartments.text =
                    it.joinToString(", ") { department -> department.name.toString() }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemCommentBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
