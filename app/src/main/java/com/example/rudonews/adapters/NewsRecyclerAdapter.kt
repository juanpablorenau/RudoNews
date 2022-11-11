package com.example.rudonews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rudonews.App
import com.example.rudonews.R
import com.example.rudonews.data.model.News
import com.example.rudonews.databinding.ItemNewsBinding

class NewsRecyclerAdapter() : RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>() {

    private lateinit var onNewsClickListener: NewsRecyclerViewInterface
    private lateinit var onFavoriteClickListener: NewsRecyclerViewInterface
    private lateinit var onShareClickListener: NewsRecyclerViewInterface

    constructor(
        onNewsClickListener: NewsRecyclerViewInterface,
        onFavoriteClickListener: NewsRecyclerViewInterface,
        onShareClickListener: NewsRecyclerViewInterface
    ) : this() {
        this.onNewsClickListener = onNewsClickListener
        this.onFavoriteClickListener = onFavoriteClickListener
        this.onShareClickListener = onShareClickListener
    }

    constructor(
        onFavoriteClickListener: NewsRecyclerViewInterface,
        onShareClickListener: NewsRecyclerViewInterface
    ) : this() {
        this.onFavoriteClickListener = onFavoriteClickListener
        this.onShareClickListener = onShareClickListener
    }

    var data = listOf<News>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])

        if (this::onNewsClickListener.isInitialized) {
            holder.itemView.setOnClickListener {
                onNewsClickListener.onNewsClickListener(data[position])
            }
        }
        if (this::onFavoriteClickListener.isInitialized) {
            val favoriteImage = holder.itemView.findViewById<ImageView>(R.id.imageView_like)
            favoriteImage.setOnClickListener {
                onFavoriteClickListener.onFavoriteClickListener(data[position])
            }
        }
        if (this::onShareClickListener.isInitialized) {
            val shareImage = holder.itemView.findViewById<ImageView>(R.id.imageView_share)
            shareImage.setOnClickListener {
                onShareClickListener.onShareClickListener(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface NewsRecyclerViewInterface {

        fun onNewsClickListener(news: News)
        fun onFavoriteClickListener(news: News)
        fun onShareClickListener(news: News)
    }

    class ViewHolder private constructor(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: News
        ) {
            binding.news = item
            binding.textDate.text = item.creation_date?.split("T")?.get(0) ?: ""

            Glide.with(itemView).load(item.image)
                .centerInside()
                .into(binding.imagePhoto)

            if (item.is_favorite == true) {
                binding.imageViewLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        App.instance,
                        R.drawable.ic_favorite
                    )
                )
            } else {
                binding.imageViewLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        App.instance,
                        R.drawable.ic_favorite_border
                    )
                )
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemNewsBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
