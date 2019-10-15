package com.pertamina.spbucare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.spbucare.databinding.ItemHistoryTopBinding
import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.ui.adapter.TopTenAdapter.ViewHolder.Companion.from

class TopTenAdapter(val clickListener: TopListener) :
    ListAdapter<Order, TopTenAdapter.ViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =  holder.bind(getItem(position), clickListener, position)

    class ViewHolder private constructor(val binding: ItemHistoryTopBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, clickListener: TopListener, position: Int) {
            binding.position = position
            binding.order = order
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) = ViewHolder(
                ItemHistoryTopBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

class TopListener(val clickListener: (history: Order, position: Int) -> Unit) {
    fun onClick(history: Order, position: Int) = clickListener(history, position)
}