package com.pertamina.spbucare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.spbucare.databinding.ItemNewsBinding
import com.pertamina.spbucare.model.News
import com.pertamina.spbucare.ui.adapter.InformationAdapter.ViewHolder.Companion.from

class InformationAdapter(val clickListener: InformationListener) :
        ListAdapter<News, InformationAdapter.ViewHolder>(InformationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: News, clickListener: InformationListener) {
            binding.news = news
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) = ViewHolder(
                    ItemNewsBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
        }
    }
}

class InformationDiffCallback : DiffUtil.ItemCallback<News>() {
    override fun areItemsTheSame(oldItem: News, newItem: News) =
            oldItem.newsId == newItem.newsId

    override fun areContentsTheSame(oldItem: News, newItem: News) =
            oldItem == newItem
}

class InformationListener(val clickListener: (information: News) -> Unit) {
    fun onClick(information: News) = clickListener(information)
}